package com.checkin.app.checkin.manager.models


import com.fasterxml.jackson.annotation.JsonProperty

data class CustomizationGroupModel(
    @JsonProperty("max_select")
    var maxSelect: Int = 0,
    @JsonProperty("min_select")
    var minSelect: Int = 0,
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("pk")
    val pk: Int = 0,
    @JsonProperty("restaurant")
    val restaurant: Int = 0
)