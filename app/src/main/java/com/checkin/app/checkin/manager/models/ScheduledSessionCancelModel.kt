package com.checkin.app.checkin.manager.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ScheduledSessionCancelModel(
        val reason: Int,
        val message: String?
)