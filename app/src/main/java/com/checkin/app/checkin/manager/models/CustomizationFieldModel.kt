package com.checkin.app.checkin.manager.models


import com.fasterxml.jackson.annotation.JsonProperty

data class CustomizationFieldModel(
    @JsonProperty("cost")
    var cost: String = "",
    @JsonProperty("is_vegetarian")
    val isVegetarian: Boolean = true,
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("pk")
    val pk: Int = 0
)