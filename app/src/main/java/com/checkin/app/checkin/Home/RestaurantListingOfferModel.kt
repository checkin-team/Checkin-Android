package com.checkin.app.checkin.Home

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantListingOfferModel(
        @JsonProperty("offer_percent") val offerPercent: Double,
        @JsonProperty("is_global") val isGlobal: Boolean,
        val code: String,
        val name: String
)