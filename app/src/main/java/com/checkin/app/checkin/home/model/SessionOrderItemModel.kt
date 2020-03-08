package com.checkin.app.checkin.home.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SessionOrderItemModel(
        val pk: Int,
        val item: ItemModel,
        val cost: String,
        val quantity: Int,
        @JsonProperty("is_customized") val isCustomized: Boolean

)