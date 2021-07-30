package com.checkin.app.checkin.manager.models


import com.fasterxml.jackson.annotation.JsonProperty

data class MenuGroupModel(
    @JsonProperty("category")
    val category: String = "",
    @JsonProperty("icon")
    val icon: String = "",
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("pk")
    val pk: Int = 0,
    @JsonProperty("restaurant")
    val restaurant: Int = 0,
    @JsonProperty("type")
    val type: String = ""
)