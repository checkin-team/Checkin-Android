package com.checkin.app.checkin.Home.model

import com.checkin.app.checkin.Home.GeolocationClass
import com.checkin.app.checkin.Home.OfferClass

data class NearbyRestaurantModel(val name: String,
                                 val phone: String,
                                 val logo: String,
                                 val covers: List<String>,
                                 val tagline: String,
                                 val locality: String,
                                 val count_checkins: Int,
                                 val geolocation: GeolocationClass,
                                 val distance:Long,
                                 val ratings:Double,
                                 val cuisines: List<String>,
                                 val offers:OfferClass

                                 )
