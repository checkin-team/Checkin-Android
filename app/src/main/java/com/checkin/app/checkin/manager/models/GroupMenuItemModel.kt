package com.checkin.app.checkin.manager.models


import com.fasterxml.jackson.annotation.JsonProperty

data class GroupMenuItemModel(
    @JsonProperty("available_meals")
    val availableMeals: List<String>,
    @JsonProperty("costs")
    val costs: List<String>,
    @JsonProperty("created")
    val created: String = "",
    @JsonProperty("customizations")
    val customizations: List<Int>,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("image")
    val image: String,
    @JsonProperty("ingredients")
    val ingredients: List<String>,
    @JsonProperty("is_available")
    val isAvailable: Boolean,
    @JsonProperty("item_type")
    val itemType: Int,
    @JsonProperty("menu")
    val menu: Int,
    @JsonProperty("modified")
    val modified: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("pk")
    val pk: Int,
    @JsonProperty("tags")
    val tags: List<String>,
    @JsonProperty("types")
    val types: List<String>
)