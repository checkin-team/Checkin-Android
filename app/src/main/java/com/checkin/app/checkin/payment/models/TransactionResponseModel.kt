package com.checkin.app.checkin.payment.models

import org.json.JSONObject

sealed class TransactionResponseModel(open val orderId: String)

data class RazorpayTxnResponseModel(
        val paymentId: String,
        override val orderId: String,
        val signature: String,
        val userEmail: String?,
        val userPhone: String?,
        val data: JSONObject?
) : TransactionResponseModel(orderId)
