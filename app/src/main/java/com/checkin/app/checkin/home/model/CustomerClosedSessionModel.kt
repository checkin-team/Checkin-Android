package com.checkin.app.checkin.home.model

import com.checkin.app.checkin.misc.models.BriefModel
import com.fasterxml.jackson.annotation.JsonProperty
import java.text.SimpleDateFormat
import java.util.*

data class CustomerClosedSessionModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("checkedin_time") val checkinTime: Date,
        @JsonProperty("count_orders") val countOrders: Int,
        @JsonProperty("count_customers") val countCustomers: Int,
        val total: Double,
        @JsonProperty("session_type") val sessionType: Int,
        val restaurant: BriefModel
) {

    fun formatId(): String = "Order ID: #${hashId}"

    fun formatTimings(): String {
        val format = SimpleDateFormat("MMM dd, YYYY hh:mm a", Locale.getDefault())
        return "${format.format(checkinTime)} | $countOrders item"
    }

    fun formatAmount() = "₹${String.format("%.2f", total)}"
}