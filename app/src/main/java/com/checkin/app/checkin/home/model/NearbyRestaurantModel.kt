package com.checkin.app.checkin.home.model

import com.checkin.app.checkin.misc.models.GeolocationModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.text.SimpleDateFormat
import java.util.*

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
        get() {
            if (is_open) {
                return "Restaurant open"
            }

            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val present = formatter.format(Calendar.getInstance().time)
            val openTime: Date = formatter.parse(timings.open)
            val closeTime: Date = formatter.parse(timings.close)
            val currentTime = formatter.parse(present)
            var day: String = if (currentTime.after(closeTime)) "tomorrow" else "today"
            val openingTime = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(openTime)
            return "Opens $day at $openingTime"
        }
}
