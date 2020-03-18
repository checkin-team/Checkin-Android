package com.checkin.app.checkin.payment.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class NewPaytmTransactionModel(
        @JsonProperty("merchant_id") val mid: String,
        @JsonProperty("order_id") val orderId: String,
        @JsonProperty("amount_paid") val amount: Double,
        @JsonProperty("txn_token") val txnToken: String,
        @JsonProperty("customer_id") val customerId: Long,
        @JsonProperty("phone") val customerPhone: String,
        @JsonProperty("email") val customerEmail: String
) : Serializable