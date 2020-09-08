package com.checkin.app.checkin.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthRequestModel(
        @JsonProperty("id_token")
        val idToken: String,
        @JsonProperty("provider_token")
        val providerToken: String
)