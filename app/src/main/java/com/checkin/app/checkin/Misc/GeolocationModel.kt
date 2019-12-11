package com.checkin.app.checkin.Misc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeolocationModel(val latitude: Double, val longitude: Double)