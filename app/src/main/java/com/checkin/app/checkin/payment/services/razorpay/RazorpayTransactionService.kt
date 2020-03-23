package com.checkin.app.checkin.payment.services.razorpay

import android.webkit.WebView
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.payment.services.ITransactionService
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.log
import com.checkin.app.checkin.utility.putAll
import com.razorpay.BaseRazorpay
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Razorpay
import org.json.JSONObject

class RazorpayTransactionService(private val razorpay: Razorpay, private val listener: TransactionListener) : ITransactionService, PaymentResultWithDataListener {
    private lateinit var mTransactionData: NewRazorpayTransactionModel
    private val baseJsonData: MutableMap<String, Any>
        get() = mutableMapOf(
                "amount" to mTransactionData.amount.toInt() * 100,
                "currency" to mTransactionData.currency,
                "order_id" to mTransactionData.orderId,
                "email" to mTransactionData.customerEmail,
                "contact" to mTransactionData.customerPhone
        )

    override fun initialize(webView: WebView, txnData: NewTransactionModel) {
        razorpay.setWebView(webView)
        mTransactionData = txnData as NewRazorpayTransactionModel
    }

    private fun initiateTransaction(data: Map<String, Any>) {
        val payload = JSONObject(data)
        razorpay.validateFields(payload, object : BaseRazorpay.ValidationListener {
            override fun onValidationError(errorData: MutableMap<String, String>?) {
                val msg = errorData?.let {
                    "Validation: ${it["field"]} - ${it["description"]}"
                } ?: "Validation failed during payment"
                onPaymentError(ERROR_CODE_INVALID_PAYLOAD, msg, null)
            }

            override fun onValidationSuccess() {
                kotlin.runCatching {
                    razorpay.submit(payload, this@RazorpayTransactionService)
                }.onFailure { it.log(TAG) }
            }
        })
    }

    override fun payByUPICollect(data: UPICollectPaymentOptionModel) {
        initiateTransaction(baseJsonData.apply {
            putAll(
                    "method" to "upi",
                    "vpa" to data.vpa
            )
        })
    }

    override fun payByUPIPush(data: UPIPushPaymentOptionModel) {
        initiateTransaction(baseJsonData.apply {
            putAll(
                    "method" to "upi",
                    "_[flow]" to "intent",
                    "upi_app_package_name" to data.packageName
            )
        })
    }

    override fun payByNetBanking(data: NetBankingPaymentOptionModel) {
        initiateTransaction(baseJsonData.apply {
            putAll(
                    "method" to "netbanking",
                    "bank" to data.bankCode
            )
        })
    }

    override fun payByCard(data: CardPaymentOptionModel) {
        initiateTransaction(baseJsonData.apply {
            putAll(
                    "method" to "card",
                    "card[name]" to data.name,
                    "card[number]" to data.cardNumber,
                    "card[expiry_month]" to data.expiryMonth,
                    "card[expiry_year]" to data.expiryYear,
                    "card[cvv]" to data.cvv
            )
        })
    }

    override fun onPaymentError(errCode: Int, errorMsg: String?, data: PaymentData?) {
        val msg = errorMsg?.let {
            runCatching { Converters.objectMapper.readTree(it)["error"]["description"].asText() }
                    .onFailure { it.log(TAG) }
                    .getOrNull()
        } ?: errorMsg
        val exc = RazorpayTransactionException(errCode, msg)
        Utils.logErrors(TAG, exc, errorMsg)
        listener.onTransactionError(exc)
    }

    override fun onPaymentSuccess(paymentId: String, data: PaymentData) {
        val extraData = data.data ?: JSONObject()
        extraData.put("user_email", data.userEmail)
        extraData.put("user_phone", data.userContact)
        listener.onTransactionResponse(RazorpayTxnResponseModel(
                paymentId, data.orderId, data.signature, extraData
        ))
    }

    companion object {
        private val TAG: String = RazorpayTransactionService::class.simpleName!!

        const val ERROR_CODE_INVALID_PAYLOAD = 1
    }
}