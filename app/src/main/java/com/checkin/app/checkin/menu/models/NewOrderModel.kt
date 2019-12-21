package com.checkin.app.checkin.menu.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewOrderModel(
        val pk: Long,
        val item: Long,
        @JsonProperty("type_index") val typeIndex: Int,
        val quantity: Int,
        val customizations: List<Int>,
        val remarks: String?
)