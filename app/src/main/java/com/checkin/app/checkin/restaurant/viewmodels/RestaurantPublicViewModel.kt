package com.checkin.app.checkin.restaurant.viewmodels

import android.app.Application
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.restaurant.RestaurantRepository
import com.checkin.app.checkin.restaurant.models.RestaurantModel

class RestaurantPublicViewModel(application: Application) : BaseViewModel(application) {
    private val mRestaurantRepository = RestaurantRepository.getInstance(application)

    private val mRestaurantData = createNetworkLiveData<RestaurantModel>()
    private var mRestaurantId = 0L

    val restaurantData
        get() = mRestaurantData

    fun fetchRestaurantWithId(restaurantId: Long) {
        mRestaurantId = restaurantId
        mRestaurantData.addSource(mRestaurantRepository.getRestaurantPublicProfile(restaurantId), mRestaurantData::setValue)
    }


    override fun fetchMissing() {
        super.fetchMissing()
        if (mRestaurantData.value?.data == null) fetchRestaurantWithId(mRestaurantId)
    }

    override fun updateResults() {

    }
}