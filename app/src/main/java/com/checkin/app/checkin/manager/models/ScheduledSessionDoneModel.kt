package com.checkin.app.checkin.manager.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ScheduledSessionDoneModel(
        val pk: Long,
        @JsonProperty("checked_out") val checkedOut: Date?
) {
    val isCheckedOut = checkedOut != null
}