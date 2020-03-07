package com.checkin.app.checkin.home.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CityLocationModel(
        val id: Int,
        val name: String,
        val state: String,
        val country: String
)