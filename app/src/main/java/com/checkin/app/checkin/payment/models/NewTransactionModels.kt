package com.checkin.app.checkin.payment.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

sealed class NewTransactionModel(
        open val amount: Double,
        open val customerPhone: String,
        open val customerEmail: String?
) : Serializable

data class NewPaytmTransactionModel(
        @JsonProperty("amount_paid") override val amount: Double,
        @JsonProperty("customer_id") val customerId: Long,
        @JsonProperty("phone") override val customerPhone: String,
        @JsonProperty("email") override val customerEmail: String?,
        @JsonProperty("merchant_id") val mid: String,
        @JsonProperty("order_id") val orderId: String,
        @JsonProperty("txn_token") val txnToken: String
) : NewTransactionModel(amount, customerPhone, customerEmail)

data class NewRazorpayTransactionModel(
        @JsonProperty("amount_paid") override val amount: Double,
        @JsonProperty("phone") override val customerPhone: String,
        @JsonProperty("email") override val customerEmail: String?,
        val currency: String,
        val receipt: String,
        @JsonProperty("order_id") val orderId: String
) : NewTransactionModel(amount, customerPhone, customerEmail)
