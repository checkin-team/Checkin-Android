package com.checkin.app.checkin.session.activesession

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.checkin.app.checkin.Data.Message.MessageModel
import com.checkin.app.checkin.Data.Message.MessageUtils
import com.checkin.app.checkin.Data.ProblemModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Misc.BaseActivity
import com.checkin.app.checkin.Misc.BillHolder
import com.checkin.app.checkin.Misc.paytm.PaytmPayment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.ShopModel
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE.PAYTM
import com.checkin.app.checkin.User.bills.SuccessfulTransactionActivity
import com.checkin.app.checkin.Utility.Constants
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptionsActivity.KEY_PAYMENT_MODE_RESULT
import com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptionsActivity.KEY_SESSION_AMOUNT
import com.checkin.app.checkin.session.model.PromoDetailModel
import com.checkin.app.checkin.session.model.SessionBillModel
import com.checkin.app.checkin.session.model.SessionInvoiceModel
import com.checkin.app.checkin.session.model.SessionPromoModel

class ActiveSessionInvoiceActivity : BaseActivity() {

    @BindView(R.id.rv_invoice_ordered_items)
    internal lateinit var rvOrderedItems: RecyclerView
    @BindView(R.id.im_invoice_waiter)
    internal lateinit var imWaiterPic: ImageView
    @BindView(R.id.tv_invoice_tip)
    internal lateinit var tvInvoiceTip: TextView
    @BindView(R.id.tv_invoice_total)
    internal lateinit var tvInvoiceTotal: TextView
    @BindView(R.id.tv_as_payment_mode)
    internal lateinit var tvPaymentMode: TextView
    @BindView(R.id.ed_invoice_tip)
    internal lateinit var edInvoiceTip: EditText
    @BindView(R.id.im_invoice_remove_promo_code)
    internal lateinit var removePromoCode: ImageView
    @BindView(R.id.tv_as_promo_applied_details)
    internal lateinit var tvAppliedPromoDetails: TextView
    @BindView(R.id.tv_as_promo_invalid_status)
    internal lateinit var tvPromoInvalidStatus: TextView
    @BindView(R.id.container_remove_promo_code)
    internal lateinit var containerRemovePromo: ViewGroup
    @BindView(R.id.container_promo_code_apply)
    internal lateinit var containerApplyPromo: ViewGroup
    @BindView(R.id.container_invoice_tip_waiter)
    internal lateinit var containerTipWaiter: ViewGroup
    @BindView(R.id.btn_invoice_request_checkout)
    internal lateinit var btnRequestCheckout: Button
    @BindView(R.id.container_as_session_benefits)
    internal lateinit var containerSessionBenefits: ViewGroup
    @BindView(R.id.tv_as_session_benefits)
    internal lateinit var tvSessionBenefits: TextView

    private var sessionId: Long = 0
    private var pendingPromoRemove = false

    private lateinit var mViewModel: ActiveSessionInvoiceViewModel
    private lateinit var mAdapter: InvoiceOrdersAdapter
    private var mBillModel: SessionBillModel? = null
    private var mBillHolder: BillHolder? = null
    private lateinit var mPrefs: SharedPreferences
    private var mPaymentPayTm: PaytmPayment? = null
    private var mPromoFragment: ActiveSessionPromoFragment? = null

    private var selectedMode: ShopModel.PAYMENT_MODE? = null

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = MessageUtils.parseMessage(intent) ?: return

            when (message.type) {
                MessageModel.MESSAGE_TYPE.USER_SESSION_PROMO_AVAILED, MessageModel.MESSAGE_TYPE.USER_SESSION_PROMO_REMOVED -> mViewModel.fetchSessionAppliedPromo()
                MessageModel.MESSAGE_TYPE.USER_SESSION_BILL_CHANGE -> mViewModel.fetchSessionInvoice()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_active_session_invoice)
        ButterKnife.bind(this)

        initProgressBar(R.id.pb_as_checkout)

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionInvoiceViewModel::class.java)

        setupUi()
        getData()
        setupObserver()
        paytmObserver()
    }

    private fun setupUi() {
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
        initRefreshScreen(R.id.sr_active_session_invoice)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val paymentTag = mPrefs.getString(Constants.SP_LAST_USED_PAYMENT_MODE, PAYTM.tag)
        selectedMode = ShopModel.PAYMENT_MODE.getByTag(paymentTag)

        setPaymentModeUpdates()
        rvOrderedItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mAdapter = InvoiceOrdersAdapter(null, null)
        rvOrderedItems.adapter = mAdapter
        mBillHolder = BillHolder(findViewById(android.R.id.content))

        mPromoFragment = ActiveSessionPromoFragment.newInstance()
    }

    private fun setPaymentModeUpdates() {
        tvPaymentMode.text = ShopModel.getPaymentMode(selectedMode!!)
        tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode!!), 0, 0, 0)
        if (selectedMode != null && selectedMode == PAYTM)
            btnRequestCheckout.setText(R.string.title_pay_checkout)
        else {
//            showPromoInvalid(R.string.label_session_offer_not_allowed_payment_mode_cash);
            btnRequestCheckout.setText(R.string.title_request_checkout)
        }
    }

    private fun getData() {
        mViewModel.fetchSessionInvoice()
        mViewModel.fetchAvailablePromoCodes()
        mViewModel.fetchSessionAppliedPromo()

        val isRequestedCheckout = intent.getBooleanExtra(KEY_SESSION_REQUESTED_CHECKOUT, false)
        if (isRequestedCheckout && !mViewModel.isRequestedCheckout)
            mViewModel.updateRequestCheckout(true)
    }

    private fun setupData(data: SessionInvoiceModel) {
        mAdapter.setData(data.orderedItems)
        mBillModel = data.bill

        if (data.host != null)
            Utils.loadImageOrDefault(imWaiterPic, data.host.displayPic, R.drawable.ic_waiter)
        else
            containerTipWaiter.visibility = View.GONE

        mBillHolder!!.bind(data.bill)
        edInvoiceTip.setText(data.bill.formatTip())
        tvInvoiceTotal.text = Utils.formatCurrencyAmount(this, data.bill.total)
    }

    private fun setupObserver() {
        mViewModel.sessionInvoice.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    setupData(resource.data)
                    tryShowTotalSavings()
                    sessionId = resource.data.pk
            }

                if (resource.status !== Resource.Status.LOADING)
                    stopRefreshing()
            }
        })

        mViewModel.checkoutData.observe(this, Observer {
            it?.let { statusModelResource ->
                if (statusModelResource.status === Resource.Status.SUCCESS && statusModelResource.data != null) {
                    Utils.toast(this, statusModelResource.data.message)
                    if (statusModelResource.data.isCheckout)
                        Utils.navigateBackToHome(applicationContext)
                else {
                        mViewModel.updateRequestCheckout(true)
                        selectedMode = ShopModel.PAYMENT_MODE.getByTag(statusModelResource.data.paymentMode)
                        when (selectedMode) {
                            PAYTM -> mViewModel.requestPaytmDetails()
                            ShopModel.PAYMENT_MODE.CASH -> finish()
                            else -> {
                            }
                    }
                }
                } else if (statusModelResource.status !== Resource.Status.LOADING) {
                    Utils.toast(this@ActiveSessionInvoiceActivity, statusModelResource.message)
                    hideProgressBar()
                    when (statusModelResource.problem?.getErrorCode()) {
                        ProblemModel.ERROR_CODE.INVALID_PAYMENT_MODE_PROMO_AVAILED -> onPaymentModeClick()
                        ProblemModel.ERROR_CODE.OFFER_REJECTED_APPLYING -> alertDialogForRejectedPromo()
                        else -> pass
                    }
                }
            }
        })

        mViewModel.requestedCheckout.observe(this, Observer {
            it?.let { isRequestedCheckout ->
                edInvoiceTip.isEnabled = (!isRequestedCheckout)

            if (isRequestedCheckout) {
                edInvoiceTip.background = resources.getDrawable(R.drawable.bordered_text_light_grey)
                edInvoiceTip.setPadding(15, 0, 0, 0)
                btnRequestCheckout.setText(R.string.session_inform_requested_checkout)
                showPromoInvalid(R.string.label_session_offer_not_allowed_session_requested_checkout)
            } else {
                setPaymentModeUpdates()
            }
            }
        })

        mViewModel.promoDeletedData.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS) {
                    showPromoApply()
                    tryShowTotalSavings()
            }
                if (pendingPromoRemove) {
                    pendingPromoRemove = false
                    onRequestCheckout(false)
                }
                Utils.toast(this, resource.message)
            }
        })

        mViewModel.sessionAppliedPromo.observe(this, Observer {
            it?.let { sessionPromoModelResource ->
                if (sessionPromoModelResource.status === Resource.Status.SUCCESS && sessionPromoModelResource.data != null) {
                    showPromoDetails(sessionPromoModelResource.data)
                    tryShowTotalSavings()
                } else if (sessionPromoModelResource.status === Resource.Status.ERROR_NOT_FOUND) {
                    showPromoApply()
            }
            }
        })

        mViewModel.promoCodes.observe(this, Observer {
            it?.let { listResource ->
                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null && listResource.data.size > 0) {
                    tryShowAvailableOffer(listResource.data.get(0))
                } else if (listResource.status === Resource.Status.ERROR_FORBIDDEN) {
                    showPromoInvalid(R.string.label_session_offer_not_allowed_phone_not_registered)
                } else if (listResource.status !== Resource.Status.LOADING) {
                    Utils.toast(this, listResource.message)
            }
            }
        })
    }

    private fun tryShowAvailableOffer(promoDetailModel: PromoDetailModel) {
        if (!mViewModel.isSessionBenefitsShown)
            showSessionBenefit(String.format("OfferClass available! %s", promoDetailModel.name))
    }

    private fun tryShowTotalSavings() {
        var savings = 0.0
        val invoiceModelResource = mViewModel.sessionInvoice.value
        if (invoiceModelResource?.data != null) {
            savings = invoiceModelResource.data.bill.totalSaving
        } else {
            val promoModelResource = mViewModel.sessionAppliedPromo.value
            if (promoModelResource?.data != null) {
                savings = promoModelResource.data.offerAmount!!
            }
        }
        if (savings > 0)
            showSessionBenefit(String.format(getString(R.string.format_session_benefits_savings), savings))
        else
            hideSessionBenefit()
    }

    private fun showPromoInvalid(@StringRes errorMsg: Int) {
        if (mViewModel.isOfferApplied)
            return
        resetPromoCards()
        tvPromoInvalidStatus.visibility = View.VISIBLE
        tvPromoInvalidStatus.setText(errorMsg)
        tvPromoInvalidStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_error_exclamation, 0, 0, 0)
        mViewModel.isSessionPromoInvalid = true
    }

    private fun showPromoApply() {
        if (mViewModel.isSessionPromoInvalid)
            return
        resetPromoCards()
        containerApplyPromo.visibility = View.VISIBLE
    }

    private fun showPromoDetails(data: SessionPromoModel) {
        resetPromoCards()
        containerRemovePromo.visibility = View.VISIBLE
        tvAppliedPromoDetails.text = data.details
    }

    private fun paytmObserver() {
        mPaymentPayTm = object : PaytmPayment() {
            override fun onPaytmTransactionResponse(inResponse: Bundle) {
                mViewModel.postPaytmCallback(inResponse)
            }

            override fun onPaytmTransactionCancel(inResponse: Bundle, msg: String) {
                mViewModel.cancelCheckoutRequest()
                Utils.toast(this@ActiveSessionInvoiceActivity, msg)
            }

            override fun onPaytmError(inErrorMessage: String) {
                mViewModel.cancelCheckoutRequest()
                Utils.toast(this@ActiveSessionInvoiceActivity, inErrorMessage)
            }
        }

        mViewModel.paytmDetails.observe(this, Observer {
            it?.let { paytmModelResource ->
                if (paytmModelResource.status === Resource.Status.SUCCESS && paytmModelResource.data != null) {
                    mPaymentPayTm!!.initializePayment(paytmModelResource.data, this)
                } else if (paytmModelResource.status !== Resource.Status.LOADING) {
                    Utils.toast(this, paytmModelResource.message)
            }
            }
        })

        mViewModel.paytmCallbackData.observe(this, Observer {
            it?.let { objectNodeResource ->
                if (objectNodeResource.status === Resource.Status.SUCCESS) {
//                Utils.navigateBackToHome(this);
                    val successIntent = Intent(this, SuccessfulTransactionActivity::class.java)
                    successIntent.putExtra(SuccessfulTransactionActivity.KEY_SESSION_ID, sessionId)
                    startActivity(successIntent)
                    finish()
                }
                if (objectNodeResource.status !== Resource.Status.LOADING) {
                    Utils.toast(this, objectNodeResource.message)
                    hideProgressBar()
            }
            }
        })

        mViewModel.sessionCancelCheckoutData.observe(this, Observer {
            it?.let { objectNodeResource ->
                if (objectNodeResource.status === Resource.Status.SUCCESS) {
                    mViewModel.updateRequestCheckout(false)
                }
                if (objectNodeResource.status !== Resource.Status.LOADING) {
                    hideProgressBar()
                }
            }
        })
    }

    @OnTextChanged(R.id.ed_invoice_tip, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTipChange(editable: Editable) {
        val amount = editable.toString().toDoubleOrNull() ?: 0.0

        if (mBillModel != null) {
            mBillModel!!.giveTip(amount)
            tvInvoiceTip.text = Utils.formatCurrencyAmount(this, mBillModel!!.tip)
            tvInvoiceTotal.text = Utils.formatCurrencyAmount(this, mBillModel!!.total)
        }
    }

    @OnClick(R.id.btn_invoice_request_checkout)
    fun onClickCheckout() {
        if (mViewModel.isRequestedCheckout)
            alertDialogForOverridingPaymentRequest()
        else
            onRequestCheckout(false)
    }

    @OnClick(R.id.container_promo_code_apply)
    fun onPromoCodeClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_promo, mPromoFragment!!)
                .addToBackStack(null)
                .commit()
    }

    @OnClick(R.id.im_invoice_remove_promo_code)
    fun onRemovePromoCode() {
        mViewModel.removePromoCode()
    }

    private fun onRequestCheckout(override: Boolean) {
        selectedMode?.let {
            mViewModel.requestCheckout(mBillModel?.tip ?: 0.0, it, override)
            visibleProgressBar()
        } ?: Utils.toast(this, "Please select the Payment Mode.")
    }

    @OnClick(R.id.container_as_payment_mode_change)
    fun onPaymentModeClick() {
        val intent = Intent(this, ActiveSessionPaymentOptionsActivity::class.java)
        intent.putExtra(KEY_SESSION_AMOUNT, tvInvoiceTotal.text.toString())
        startActivityForResult(intent, REQUEST_PAYMENT_MODE)
    }

    @OnClick(R.id.im_session_view_invoice_back)
    fun onBackClick() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_PAYMENT_MODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                selectedMode = data.getSerializableExtra(KEY_PAYMENT_MODE_RESULT) as ShopModel.PAYMENT_MODE
                mPrefs.edit()
                        .putString(Constants.SP_LAST_USED_PAYMENT_MODE, selectedMode!!.tag)
                        .apply()

                setPaymentModeUpdates()
                tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode!!), 0, 0, 0)
            }
            else -> {
            }
        }
    }

    private fun showSessionBenefit(msg: String) {
        containerSessionBenefits.visibility = View.VISIBLE
        tvSessionBenefits.text = Html.fromHtml(msg)
        mViewModel.showedSessionBenefits()
    }

    private fun hideSessionBenefit() {
        containerSessionBenefits.visibility = View.GONE
    }

    private fun resetPromoCards() {
        containerRemovePromo.visibility = View.GONE
        containerApplyPromo.visibility = View.GONE
        tvPromoInvalidStatus.visibility = View.GONE
        tvPromoInvalidStatus.setText(R.string.active_session_fetching_offers)
        tvPromoInvalidStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    private fun alertDialogForOverridingPaymentRequest() {
        val builder = AlertDialog.Builder(this).setTitle("Override checkout request?")
                .setMessage(R.string.label_session_transaction_inprogress)
                .setPositiveButton("Yes") { dialog, which -> onRequestCheckout(true) }
                .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun alertDialogForRejectedPromo() {
        val builder = AlertDialog.Builder(this).setTitle("Continue without OfferClass?")
                .setMessage(R.string.label_session_promo_cannot_be_applied)
                .setPositiveButton("Yes") { dialog, which ->
                    // Remove the promo and once done, request checkout again
                    onRemovePromoCode()
                    pendingPromoRemove = true
                }
                .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        val types = arrayOf(MessageModel.MESSAGE_TYPE.USER_SESSION_PROMO_AVAILED, MessageModel.MESSAGE_TYPE.USER_SESSION_PROMO_REMOVED, MessageModel.MESSAGE_TYPE.USER_SESSION_BILL_CHANGE)
        MessageUtils.registerLocalReceiver(this, mReceiver, *types)
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(this, mReceiver)
    }

    override fun updateScreen() {
        mViewModel.updateResults()
    }

    companion object {
        const val KEY_SESSION_REQUESTED_CHECKOUT = "invoice.session.requested_checkout"
        private const val REQUEST_PAYMENT_MODE = 141
    }
}
