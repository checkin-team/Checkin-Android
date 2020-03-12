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
import com.checkin.app.checkin.home.model.ClosedSessionDetailsModel
import com.checkin.app.checkin.home.model.CustomerClosedSessionModel
import com.checkin.app.checkin.utility.SingletonHolder

class HomeRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    val customerClosedSessionList: LiveData<Resource<List<CustomerClosedSessionModel>>> =
            object : NetworkBoundResource<List<CustomerClosedSessionModel>, List<CustomerClosedSessionModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

                override fun createCall(): LiveData<ApiResponse<List<CustomerClosedSessionModel>>> {
                    return RetrofitLiveData(mWebService.customerClosedTransactions)
            }

        }.asLiveData

    fun getCustomerClosedSessionDetails(sessionId: Long): LiveData<Resource<ClosedSessionDetailsModel>> =
            object : NetworkBoundResource<ClosedSessionDetailsModel, ClosedSessionDetailsModel>() {
                override fun shouldUseLocalDb(): Boolean = false

                override fun createCall(): LiveData<ApiResponse<ClosedSessionDetailsModel>> {
                    return RetrofitLiveData(mWebService.getCustomerClosedSessionDetails(sessionId))
                }

            }.asLiveData


    companion object : SingletonHolder<HomeRepository, Application>({ HomeRepository(it.applicationContext) })
}