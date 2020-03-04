package com.checkin.app.checkin.home.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CityLocationModel(
        @JsonProperty("name") val name: String,
        @JsonProperty("state") val state: String,
        @JsonProperty("country") val country: String
)