package com.checkin.app.checkin.session.scheduled

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Account.AccountUtil
import com.checkin.app.checkin.Data.ProblemModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.LockableBottomSheetBehavior
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.menu.controllers.CartOrderedItemController
import com.checkin.app.checkin.menu.holders.CartOrderInteraction
import com.checkin.app.checkin.menu.models.CartDetailModel
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.misc.BillHolder
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.SessionPromoModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ScheduledSessionCartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CartOrderInteraction, TextWatcher {
    @BindView(R.id.container_cart_header_topbar)
    internal lateinit var containerCartTopbar: ViewGroup
    @BindView(R.id.tv_cart_header_planned_time)
    internal lateinit var tvHeaderPlannedTime: TextView
    @BindView(R.id.container_cart_header_time_switcher)
    internal lateinit var containerTimeSwitcher: ViewGroup
    @BindView(R.id.epoxy_rv_scheduled_cart_orders)
    internal lateinit var epoxyRvCartOrders: EpoxyRecyclerView
    @BindView(R.id.nested_sv_cart)
    internal lateinit var nestedSvCart: NestedScrollView
    @BindView(R.id.tv_cart_guest_detail_name)
    internal lateinit var tvGuestName: TextView
    @BindView(R.id.et_cart_scheduled_remarks)
    internal lateinit var etRemarks: EditText
    @BindView(R.id.tv_cart_footer_pay_total)
    internal lateinit var tvPayTotal: TextView
    @BindView(R.id.tv_cart_invoice_total)
    internal lateinit var tvInvoiceTotal: TextView
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

    lateinit var activity: FragmentActivity
    lateinit var viewModel: CartViewModel
    lateinit var networkViewModel: BlockingNetworkViewModel
    lateinit var listener: ScheduledSessionInteraction
    lateinit var scheduledSessionViewModel: ScheduledSessionViewModel

    private val ordersController = CartOrderedItemController(this)
    private val billHolder: BillHolder
    private lateinit var bottomSheetBehavior: LockableBottomSheetBehavior<View>

    private var isSessionPromoInvalid = false

    init {
        View.inflate(context, R.layout.fragment_scheduled_session_cart, this).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white_two))
            ButterKnife.bind(this@ScheduledSessionCartView)
        }
        billHolder = BillHolder(this)
        setupUi()
    }

    private fun setupUi() {
        nestedSvCart.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> requestDisallowInterceptTouchEvent(false)
            }
            v.onTouchEvent(event)
            true
        }
        epoxyRvCartOrders.setHasFixedSize(false)
        epoxyRvCartOrders.setControllerAndBuildModels(ordersController)
    }

    private fun showScheduler() {
        listener.onCreateNewScheduledSession()
    }

    fun isExpanded() = bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED

    fun dismiss() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @OnClick(R.id.container_cart_header_topbar)
    fun onClickedTopbar() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    @OnClick(R.id.container_cart_header_time_switcher)
    fun switchTime() {
        viewModel.cartDetailData.value?.data?.let {
            listener.updateSessionTime(it.scheduled)
        }
    }

    @OnClick(R.id.container_cart_footer_pay_button)
    fun onClickPayment() {
        listener.onStartPayment()
    }

    fun setup(activity: FragmentActivity) {
        this.activity = activity
        listener = activity as ScheduledSessionInteraction
        viewModel = ViewModelProviders.of(activity)[CartViewModel::class.java]
        networkViewModel = ViewModelProviders.of(activity)[BlockingNetworkViewModel::class.java]
        scheduledSessionViewModel = ViewModelProviders.of(activity)[ScheduledSessionViewModel::class.java]

        bottomSheetBehavior = BottomSheetBehavior.from(this) as LockableBottomSheetBehavior<View>
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) = when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheetBehavior.swipeEnabled = false
                    if (viewModel.sessionPk == 0L) {
                        showScheduler()
                    } else if (!scheduledSessionViewModel.isPhoneVerified) {
                        listener.onVerifyPhoneOfUser()
                    }
                    listener.onCartOpen()
                }
                else -> {
                    bottomSheetBehavior.swipeEnabled = true
                    listener.onCartClose()
                }
            }
        })
        etRemarks.addTextChangedListener(this)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.serverPendingData.observe(activity, Observer {
            it?.let { listResource ->
                networkViewModel.updateStatus(listResource)
                when (listResource.status) {
                    Resource.Status.SUCCESS -> {
                        viewModel.fetchCartBill()
                    }
                }
            }
        })
        viewModel.orderedItems.observe(activity, Observer {
            it?.let {
                ordersController.orderedItems = it
            }
        })
        viewModel.cartBillData.observe(activity, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> resource.data?.also {
                        billHolder.bind(it.bill)
                        setTotal(it.bill.total)
                    }
                    Resource.Status.LOADING -> billHolder.showLoading()
                    else -> pass
                }
            }
        })
        viewModel.cartDetailData.observe(activity, Observer {
            it?.let { resource ->
                networkViewModel.updateStatus(resource)
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    setupData(resource.data)
                }
            }
        })
        networkViewModel.shouldTryAgain.observe(activity, Observer {
            it?.let { className ->
                // Only retry ordering the current item if not related to cart detail
                if (className != CartDetailModel::class.java.simpleName) viewModel.retryOrder()
            }
        })

        scheduledSessionViewModel.sessionAppliedPromo.observe(activity, Observer {
            it?.let { sessionPromoModelResource ->
                if (sessionPromoModelResource.status === Resource.Status.SUCCESS && sessionPromoModelResource.data != null) {
                    showPromoDetails(sessionPromoModelResource.data)
                    viewModel.fetchCartBill()
//                    tryShowTotalSavings()
                } else if (sessionPromoModelResource.status === Resource.Status.ERROR_NOT_FOUND) {
                    showPromoApply()
                } else if (sessionPromoModelResource.problem?.getErrorCode() == ProblemModel.ERROR_CODE.USER_MISSING_PHONE) {
                    scheduledSessionViewModel.isPhoneVerified = false
                    if (isExpanded()) listener.onVerifyPhoneOfUser()
                }
            }
        })
        scheduledSessionViewModel.promoDeletedData.observe(activity, Observer {
            if (it?.status == Resource.Status.SUCCESS) viewModel.fetchCartBill()
        })
    }

    private fun showPromoApply() {
        if (isSessionPromoInvalid)
            return
        resetPromoCards()
        containerApplyPromo.visibility = View.VISIBLE
    }

    private fun showPromoDetails(data: SessionPromoModel) {
        resetPromoCards()
        containerRemovePromo.visibility = View.VISIBLE
        tvAppliedPromoDetails.text = data.details
    }

    private fun resetPromoCards() {
        containerRemovePromo.visibility = View.GONE
        containerApplyPromo.visibility = View.GONE
        tvPromoInvalidStatus.visibility = View.GONE
        tvPromoInvalidStatus.setText(R.string.active_session_fetching_offers)
        tvPromoInvalidStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    private fun setupData(data: CartDetailModel) {
        tvHeaderPlannedTime.text = "${data.scheduled.formatPlannedDateTime}, Table for ${data.scheduled.countPeople}"
        etRemarks.removeTextChangedListener(this)
        etRemarks.setText(data.scheduled.remarks ?: "")
        etRemarks.addTextChangedListener(this)
        tvGuestName.text = AccountUtil.getUsername(context)
        billHolder.bind(data.bill)
        setTotal(data.bill.total)
    }

    private fun setTotal(total: Double) {
        tvPayTotal.text = Utils.formatCurrencyAmount(context, total)
        tvInvoiceTotal.text = Utils.formatCurrencyAmount(context, total)
    }

    override fun onItemChange(orderedItemModel: OrderedItemModel, newCount: Int) {
        val order = orderedItemModel.updateQuantity(newCount)
        viewModel.orderItem(order)
    }

    @OnClick(R.id.container_promo_code_apply)
    fun onPromoCodeClick() {
        listener.onOpenPromoList()
    }

    @OnClick(R.id.im_invoice_remove_promo_code)
    fun onRemovePromoCode() {
        scheduledSessionViewModel.removePromoCode()
    }

    override fun afterTextChanged(s: Editable?) {
        scheduledSessionViewModel.updateScheduledSessionRemarks(s?.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

interface ScheduledSessionInteraction {
    fun updateSessionTime(scheduled: ScheduledSessionDetailModel)
    fun onCreateNewScheduledSession()
    fun onCartOpen()
    fun onCartClose()
    fun onOpenPromoList()
    fun onVerifyPhoneOfUser()
    fun onStartPayment()
}
