package com.checkin.app.checkin.manager.models


import com.fasterxml.jackson.annotation.JsonProperty

data class CatalogMenuModel(
    @JsonProperty("created")
    val created: String = "",
    @JsonProperty("is_available")
    val isAvailable: Boolean = false,
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("pk")
    val pk: Int = 0,
    @JsonProperty("restaurant")
    val restaurant: Int = 0
)