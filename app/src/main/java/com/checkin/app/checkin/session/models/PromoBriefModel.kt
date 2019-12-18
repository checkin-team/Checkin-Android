package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PromoBriefModel(
        val code: String,
        val icon: String?,
        val name: String
)
