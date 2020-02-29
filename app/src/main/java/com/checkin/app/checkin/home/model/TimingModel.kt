package com.checkin.app.checkin.home.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimingModel(
        val open: String,
        val close: String
)