package com.checkin.app.checkin.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.model.CityLocationModel
import com.checkin.app.checkin.utility.SingletonHolder

class HomeRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    val getAllCities: LiveData<Resource<List<CityLocationModel>>>
        get() {
            return object : NetworkBoundResource<List<CityLocationModel>, List<CityLocationModel>>() {
                override fun shouldUseLocalDb(): Boolean = false

                override fun createCall(): LiveData<ApiResponse<List<CityLocationModel>>> {
                    return RetrofitLiveData(mWebService.getCities)
                }
            }.asLiveData
        }

    companion object : SingletonHolder<HomeRepository, Application>({ HomeRepository(it.applicationContext) })
}