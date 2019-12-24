package com.checkin.app.checkin.Home.model

import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.models.GeolocationModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class NearbyRestaurantModel(
        val pk: Long,
        val name: String,
        val phone: String,
        val logo: String?,
        val covers: List<String?>,
        val tagline: String?,
        val locality: String?,
        @JsonProperty("count_checkins") val countCheckins: Long,
        val geolocation: GeolocationModel?,
        val distance: Double,
        val ratings: Double,
        val cuisines: List<String>,
        val offer: RestaurantListingOfferModel?
) {
    val formatDistance: String
        get() = "$distance ${if (distance <= 1.0) "km" else "kms"}"

    val formatCheckins: String
        get() = "Checkins ${Utils.formatCount(countCheckins)}"
}
