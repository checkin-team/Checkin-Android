package com.checkin.app.checkin.restaurant

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.*
import com.checkin.app.checkin.Utility.SingletonHolder
import com.checkin.app.checkin.restaurant.models.RestaurantModel

class RestaurantRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService = ApiClient.getApiService(context)

    fun getRestaurantPublicProfile(restaurantId: Long): LiveData<Resource<RestaurantModel>> {
        return object : NetworkBoundResource<RestaurantModel, RestaurantModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<RestaurantModel>> = RetrofitLiveData(mWebService.getRestaurantProfile(restaurantId))

            override fun saveCallResult(data: RestaurantModel?) {
            }

        }.asLiveData
    }

    companion object : SingletonHolder<RestaurantRepository, Application>({ RestaurantRepository(it.applicationContext) })
}