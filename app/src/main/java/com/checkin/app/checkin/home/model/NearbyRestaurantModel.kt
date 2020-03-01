package com.checkin.app.checkin.home.model

import com.checkin.app.checkin.misc.models.GeolocationModel
import com.checkin.app.checkin.utility.Utils
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
        val offer: RestaurantListingOfferModel?,
        val is_open: Boolean,
        val timings: TimingModel
) {
    val formatDistance: String
        get() = "$distance ${if (distance <= 1.0) "km" else "kms"}"

    val formatCheckins: String
        get() = if (countCheckins < 100) "New" else "Checkins ${Utils.formatCount(countCheckins)}"

    val formatRating: String
        get() = if (ratings < 1.0) "---" else ratings.toString()

    val formatOpeningTimings: String
        get() =
            if (is_open) "Restaurant open" else Utils.formatOpenTimings(timings.open, timings.close)
}
