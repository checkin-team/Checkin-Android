package com.checkin.app.checkin.manager.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GuestDetailsModel(
        @JsonProperty("phone") var contact: String,
        var name: String
)