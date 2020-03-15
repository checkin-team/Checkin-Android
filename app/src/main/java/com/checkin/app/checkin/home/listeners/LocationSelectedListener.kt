package com.checkin.app.checkin.home.listeners

import com.checkin.app.checkin.home.model.CityLocationModel

interface LocationSelectedListener {
    fun onLocationSelected(data: CityLocationModel?)
}