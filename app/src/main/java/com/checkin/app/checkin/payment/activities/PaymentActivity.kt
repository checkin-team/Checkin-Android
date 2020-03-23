package com.checkin.app.checkin.payment.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.navigation.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.payment.services.TransactionException
import com.checkin.app.checkin.payment.services.paymentLocator
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.toCurrency
import com.checkin.app.checkin.utility.toast

class PaymentActivity : BaseActivity(), TransactionListener {
    @BindView(R.id.tv_payment_toolbar_amount)
    internal lateinit var tvAmountToolbar: TextView
    @BindView(R.id.wv_payment_process)
    internal lateinit var wvPaymentProcess: WebView

    private val viewModel: PaymentViewModel by viewModels()
    private val txnService by lazy { paymentLocator.getTransactionService(this) }
    private val networkViewModel: BlockingNetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ButterKnife.bind(this)

        setSupportActionBar(findViewById(R.id.toolbar_payment))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val txnData = intent.getSerializableExtra(KEY_TRANSACTION_MODEL) as NewTransactionModel
        viewModel.init(txnData)

        tvAmountToolbar.text = txnData.amount.toCurrency(this)
        txnService.initialize(wvPaymentProcess, txnData)
        findNavController(R.id.nav_host_payment).setGraph(R.navigation.nav_payment)

        setupObserver()
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, NetworkBlockingFragment.withBlockingLoader(), NetworkBlockingFragment.FRAGMENT_TAG)
        }
    }

    private fun setupObserver() {
        viewModel.fetchPaymentMethods()
        viewModel.onRequestPay {
            wvPaymentProcess.visibility = View.VISIBLE
            when (it) {
                is UPICollectPaymentOptionModel -> txnService.payByUPICollect(it)
                is UPIPushPaymentOptionModel -> txnService.payByUPIPush(it)
                is NetBankingPaymentOptionModel -> txnService.payByNetBanking(it)
                is CardPaymentOptionModel -> txnService.payByCard(it)
            }
        }
        viewModel.onPaymentCallback {
            networkViewModel.updateStatus(it)
            if (it.status == Resource.Status.SUCCESS) {
                setResult(RESULT_PAID)
                finish()
            } else if (it.status != Resource.Status.LOADING) {
                toast(it.message)
            }
        }
    }

    override fun onTransactionResponse(data: TransactionResponseModel) {
        toast("Success Payment!")
        viewModel.savePaymentOption()
        viewModel.callPaymentCallback(data)
        resetWebView()
    }

    override fun onTransactionCancel(msg: String?) {
        resetWebView()
    }

    override fun onTransactionError(error: TransactionException) {
        toast(error.localizedMessage)
        resetWebView()
    }

    private fun resetWebView() {
        wvPaymentProcess.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (wvPaymentProcess.visibility != View.VISIBLE || onDoubleBackPressed()) {
            setResult(RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        kotlin.runCatching {
            paymentLocator.paymentProvider.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_PAID = 1

        const val KEY_TRANSACTION_MODEL = "payment.transaction"

        fun withTransactionIntent(context: Context, txnModel: NewTransactionModel) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(KEY_TRANSACTION_MODEL, txnModel)
        }
    }
}