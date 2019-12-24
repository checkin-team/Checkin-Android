package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class QRResultModel(
        @JsonProperty("session_pk") val sessionPk: Long = 0,
        @JsonProperty("restaurant_pk") val restaurantPk: Long = 0,
        val detail: String? = null,
        val table: String? = null,
        @JsonProperty("is_master_qr") val isMasterQr: Boolean
)