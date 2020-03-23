package com.checkin.app.checkin.payment.models

import com.checkin.app.checkin.data.Converters
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.json.JSONObject

sealed class TransactionResponseModel(open val orderId: String)

data class RazorpayTxnResponseModel(
        @JsonProperty("payment_id") val paymentId: String,
        @JsonProperty("order_id") override val orderId: String,
        val signature: String,
        @JsonProperty("extra_data")
        @JsonSerialize(using = Converters.JsonObjectSerializer::class)
        val data: JSONObject?
) : TransactionResponseModel(orderId)
