package com.checkin.app.checkin.home.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemModel(
        val pk: Int,
        val name: String,
        @JsonProperty("is_vegetarian") val isVegetarian: Boolean?
)
