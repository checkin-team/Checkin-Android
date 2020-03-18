package com.checkin.app.checkin.payment.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.checkin.app.checkin.payment.services.paytm.PaytmTransactionService
import com.checkin.app.checkin.utility.pass
import com.checkin.app.checkin.utility.toCurrency
import net.one97.paytm.nativesdk.Constants.SDKConstants

class PaymentActivity : BaseActivity(), TransactionListener {
    @BindView(R.id.tv_payment_toolbar_amount)
    internal lateinit var tvAmountToolbar: TextView

    private val viewModel: PaymentViewModel by viewModels()
    private val txnService: PaytmTransactionService by lazy { PaytmTransactionService(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ButterKnife.bind(this)

        setSupportActionBar(findViewById(R.id.toolbar_payment))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val txnData = intent.getSerializableExtra(KEY_TRANSACTION_MODEL) as NewPaytmTransactionModel
        viewModel.init(txnData)

        tvAmountToolbar.text = txnData.amount.toCurrency(this)
        txnService.initialize(txnData.txnToken, txnData.amount, txnData.mid, txnData.orderId)
        findNavController(R.id.nav_host_payment).setGraph(R.navigation.nav_payment)

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.onRequestPay {
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
    }

    override fun onTransactionCancel(msg: String?) {
        println("CANCELLED ==== $msg")
    }

    override fun onTransactionError(error: Throwable) {
        error.printStackTrace()
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, Intent())
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SDKConstants.PAYACTIVITY_REQUEST_CODE) {
            // Back from UPI PUSH payment
            txnService.confirmUPIPushPayment(this)
        }
        println("UPIPUSH ==== $requestCode, $resultCode, $data")
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val PAYMENT_SUCESSFULL = 1
        const val PAYMENT_AMOUNT_ZERO = 2
        const val PAYMENT_FAILED = 4

        const val KEY_TRANSACTION_MODEL = "payment.transaction"

        fun withTransactionIntent(context: Context, txnModel: NewPaytmTransactionModel) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(KEY_TRANSACTION_MODEL, txnModel)
        }
    }
}