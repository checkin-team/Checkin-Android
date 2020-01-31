package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledSessionDetailModel(
        @JsonProperty("count_people") val countPeople: Int,
        @JsonProperty("planned_datetime") val plannedDatetime: Date?,
        val remarks: String?,
        @JsonProperty("order_time") val orderTime: Date?,
        @JsonProperty("preparation_time") val preparationTime: Date?,
        val modified: Date
) {
    lateinit var status: ScheduledSessionStatus

    @JsonProperty("status")
    fun setStatusCode(statusCode: Int) {
        status = ScheduledSessionStatus.getById(statusCode)
    }

    val formatGuestCount: String = "Table for $countPeople"

    val formatPlannedDate: String
        get() = Utils.formatDate(plannedDatetime ?: orderTime!!, "MMM dd")

    val formatPlannedTime: String
        get() = Utils.formatDateTo12HoursTime(plannedDatetime!!)

    val formatOrderTime: String
        get() = Utils.formatDateTo12HoursTime(orderTime!!)

    val formatPlannedDateTime: String?
        get() = "$formatPlannedDate, $formatPlannedTime"

    val formatOrderElapsedTime: String
        get() = Utils.formatElapsedTime(orderTime!!)
}
