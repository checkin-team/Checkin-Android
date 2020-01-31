package com.checkin.app.checkin.Shop.Private.Invoice

import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE
import com.checkin.app.checkin.user.models.UserBriefModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.io.Serializable
import java.util.*

data class RestaurantSessionModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("count_orders") val countOrders: Int = 0,
        @JsonProperty("count_customers") val countCustomers: Int = 0,
        val total: Double,
        val host: UserBriefModel?,
        val table: String?,
        @JsonProperty("checked_in") val checkedIn: Date?,
        @JsonProperty("checked_out") val checkedOut: Date,
        @JsonProperty("payment_mode")
        @JsonDeserialize(using = PAYMENT_MODE.PaymentModeDeserializer::class)
        val paymentMode: PAYMENT_MODE?,
        @JsonProperty("checkedin_time") val workingCheckedinTime: Date,
        @JsonProperty("is_scheduled") val isScheduled: Boolean
) : Serializable {

    val formatHashId = "${if (isScheduled) "Booked" else "Session"} ID: #$hashId"

    val formattedDate: String = Utils.formatCompleteDate(if (isScheduled) workingCheckedinTime else checkedOut)

    val tableInfo: String = table ?: "Booked"

    fun formatTotal(): String = total.toString()
}