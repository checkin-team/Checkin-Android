package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.Utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledSessionDetailModel(
        @JsonProperty("count_people") val countPeople: Int,
        @JsonProperty("planned_datetime") val plannedDatetime: Date,
        val remarks: String?,
        @JsonProperty("preparation_time") val preparationTime: Date?
) {
    lateinit var status: ScheduledSessionStatus

    @JsonProperty("status")
    fun setStatusCode(statusCode: Int) {
        status = ScheduledSessionStatus.getById(statusCode)
    }

    val formatPlannedTime: String = Utils.formatDate(plannedDatetime, "dd MMM, HH:mm a")
}
