package com.checkin.app.checkin.payment.services.razorpay

import android.content.Context
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.payment.services.ITransactionService
import com.checkin.app.checkin.payment.services.Transaction
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.putAll
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Razorpay
import org.json.JSONObject

class RazorpayTransactionService(activity: AppCompatActivity, private val listener: TransactionListener) : ITransactionService, PaymentResultWithDataListener {
    private val razorpay by lazy {
        Razorpay(activity, RemoteConfig[RemoteConfig.Constants.KEY_RAZORPAY].asString())
    }
    private lateinit var mTransactionData: NewRazorpayTransactionModel
    private val baseJsonData: MutableMap<String, Any>
        get() = mutableMapOf(
                "amount" to mTransactionData.amount.toInt() * 100,
                "currency" to mTransactionData.currency,
                "order_id" to mTransactionData.orderId,
                "email" to mTransactionData.customerEmail,
                "contact" to mTransactionData.customerPhone
        )

    override val transaction: Transaction = razorpay

    override fun initialize(webView: WebView, txnData: NewTransactionModel) {
        razorpay.setWebView(webView)
        mTransactionData = txnData as NewRazorpayTransactionModel
    }

    override fun payByUPICollect(data: UPICollectPaymentOptionModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun payByUPIPush(data: UPIPushPaymentOptionModel) {
        val jsonData = JSONObject(baseJsonData.apply {
            putAll(
                    "method" to "upi",
                    "_[flow]" to "intent",
                    "upi_app_package_name" to data.packageName
            )
        })
        razorpay.submit(jsonData, this)
    }

    override fun confirmUPIPushPayment(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun payByNetBanking(context: Context, data: NetBankingPaymentOptionModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPaymentError(errCode: Int, errorMsg: String?, data: PaymentData?) {
        val exc = RazorpayTransactionException(errCode, errorMsg)
        Utils.logErrors(TAG, exc, errorMsg)
        listener.onTransactionError(exc)
    }

    override fun onPaymentSuccess(paymentId: String, data: PaymentData) {
        listener.onTransactionResponse(RazorpayTxnResponseModel(
                paymentId, data.orderId, data.signature, data.userEmail,
                data.userContact, data.data
        ))
    }

    companion object {
        private val TAG = RazorpayTransactionService::class.simpleName
    }
}