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
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.payment.services.PaymentServiceLocator
import com.checkin.app.checkin.utility.pass
import com.checkin.app.checkin.utility.toCurrency

class PaymentActivity : BaseActivity(), TransactionListener {
    @BindView(R.id.tv_payment_toolbar_amount)
    internal lateinit var tvAmountToolbar: TextView
    @BindView(R.id.wv_payment_process)
    internal lateinit var wvPaymentProcess: WebView

    private val viewModel: PaymentViewModel by viewModels()
    private val txnService by lazy { PaymentServiceLocator.getTransactionService(this, this) }

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
        viewModel.fetchNetBankingOptions(txnService.transaction)
    }

    private fun setupObserver() {
        viewModel.onRequestPay {
            wvPaymentProcess.visibility = View.VISIBLE
            when (it) {
                is UPICollectPaymentOptionModel -> txnService.payByUPICollect(it)
                is UPIPushPaymentOptionModel -> txnService.payByUPIPush(it)
                is NetBankingPaymentOptionModel -> txnService.payByNetBanking(this, it)
                else -> pass
            }
        }
    }

    override fun onTransactionResponse(data: TransactionResponseModel) {
        println("RESPONSE ==== $data")
        resetWebView()
    }

    override fun onTransactionCancel(msg: String?) {
        println("CANCELLED ==== $msg")
        resetWebView()
    }

    override fun onTransactionError(error: Throwable) {
        error.printStackTrace()
        resetWebView()
    }

    private fun resetWebView() {
        wvPaymentProcess.visibility = View.GONE
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, Intent())
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val RESULT_CANCELED = 0

        const val KEY_TRANSACTION_MODEL = "payment.transaction"

        fun withTransactionIntent(context: Context, txnModel: NewTransactionModel) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(KEY_TRANSACTION_MODEL, txnModel)
        }
    }
}