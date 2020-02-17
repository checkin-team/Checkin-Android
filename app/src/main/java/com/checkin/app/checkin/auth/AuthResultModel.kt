package com.checkin.app.checkin.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResultModel(
        @JsonProperty("user_id")
        val userId: Long,
        @JsonProperty("token")
        val token: String
)