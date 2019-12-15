package com.checkin.app.checkin.Shop

import com.checkin.app.checkin.misc.models.GeolocationModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantLocationModel(
        val pk: Long,
        val name: String,
        val phone: String?,
        val locality: String?,
        val logo: String?,
        val geolocation: GeolocationModel?
)