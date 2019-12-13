package com.checkin.app.checkin.session.model

import com.checkin.app.checkin.Utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledSessionBriefModel(
        @JsonProperty("count_people") val countPeople: Int,
        @JsonProperty("planned_datetime") val plannedDatetime: Date
) {
    lateinit var status: ScheduledSessionStatus

    @JsonProperty("status")
    fun setStatusCode(statusCode: Int) {
        status = ScheduledSessionStatus.getById(statusCode)
    }

    val formatDate: String
        get() = Utils.formatDate(plannedDatetime, "dd MMMM YYY")

    val formatTime: String
        get() = Utils.formatDateTo12HoursTime(plannedDatetime)
}

enum class ScheduledSessionStatus(val id: Int) {
    NONE(0), PENDING(3), CANCELLED_BY_RESTAURANT(8),
    CANCELLED_BY_USER(9), ACCEPTED(5), DONE(10);

    val repr: String
        get() = when (this) {
            PENDING -> "Pending"
            ACCEPTED -> "Accepted"
            CANCELLED_BY_USER, CANCELLED_BY_RESTAURANT -> "Cancelled"
            DONE -> "Done"
            else -> "Unknown"
        }

    companion object {
        fun getById(id: Int): ScheduledSessionStatus = values().find { it.id == id } ?: NONE
    }
}
