package com.checkin.app.checkin.Shop

import com.checkin.app.checkin.Misc.GeolocationModel

data class RestaurantLocationModel(
        val name: String,
        val phone: String?,
        val locality: String?,
        val logo: String?,
        val geolocation: GeolocationModel?
)