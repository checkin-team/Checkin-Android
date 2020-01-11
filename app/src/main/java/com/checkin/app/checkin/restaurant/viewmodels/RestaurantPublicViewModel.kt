package com.checkin.app.checkin.restaurant.viewmodels

import android.app.Application
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.restaurant.RestaurantRepository
import com.checkin.app.checkin.restaurant.models.RestaurantModel

class RestaurantPublicViewModel(application: Application) : BaseViewModel(application) {
    private val mRestaurantRepository = RestaurantRepository.getInstance(application)

    private val mRestaurantData = createNetworkLiveData<RestaurantModel>()

    val restaurantData
        get() = mRestaurantData

    fun fetchRestaurantWithId(restaurantId: Long) {
        mRestaurantData.addSource(mRestaurantRepository.getRestaurantPublicProfile(restaurantId), mRestaurantData::setValue)
    }

    override fun updateResults() {

    }
}