package com.checkin.app.checkin.restaurant.models

import com.checkin.app.checkin.misc.models.LocationModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

sealed class ShopModel(
        open val pk: Long,
        open var name: String,
        open var tagline: String?,
        open val logo: String?,
        open val covers: ArrayList<String?>?,
        open var phone: String?,
        open var distance: Double,
        open var email: String?,
        open val location: LocationModel?,
        open val gstin: String?,
        open var website: String?,
        val rating: Float = 0f,
        @JsonProperty("is_active") open var isActive: Boolean = true,
        @JsonProperty("is_verified") open var isVerified: Boolean = true
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantModel(
        override val pk: Long,
        override var name: String,
        override var tagline: String?,
        override val logo: String?,
        override val covers: ArrayList<String?>?,
        override var email: String?,
        override val gstin: String?,
        override var website: String?,
        override var phone: String?,
        override var distance: Double,
        override val location: LocationModel?,
        val cuisines: ArrayList<String>?,
        val categories: ArrayList<String>?,
        val restaurantOffers: String?,
        @JsonProperty("has_nonveg") val hasNonVeg: Boolean = false,
        @JsonProperty("has_home_delivery") val hasHomeDelivery: Boolean = false,
        @JsonProperty("no_followers") val followers: Long,
        @JsonProperty("count_checkins") val checkins: Long,
        @JsonProperty("no_reviews") val countReviews: Long,
        @JsonProperty("extra_data") val extraData: List<String>?
) : ShopModel(pk, name, tagline, logo, covers, phone,distance, email, location, gstin, website), Serializable {
    fun formatFollowers(): String? = Utils.formatCount(followers)

    fun formatCheckins(): String? = if (checkins < 100) "New" else "Checkins ${Utils.formatCount(checkins)}"

    fun formatReviews(): String? = Utils.formatCount(countReviews)

    fun formatRating(): String? = if (rating < 1.0) "---" else rating.toString()

    val formatDistance: String
        get() = "$distance ${if (distance <= 1.0) "km" else "kms"}"
}