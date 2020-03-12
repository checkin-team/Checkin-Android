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
import com.checkin.app.checkin.home.model.PastSessionDetailsModel
import com.checkin.app.checkin.session.models.CustomerPastSessionModel
import com.checkin.app.checkin.utility.SingletonHolder

class HomeRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    val customerPastSessionList: LiveData<Resource<List<CustomerPastSessionModel>>> =
            object : NetworkBoundResource<List<CustomerPastSessionModel>, List<CustomerPastSessionModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<CustomerPastSessionModel>>> {
                return RetrofitLiveData(mWebService.customerPastTransactions)
            }

        }.asLiveData

    fun getCustomerPastSessionDetails(sessionId:Long): LiveData<Resource<PastSessionDetailsModel>> =
            object :NetworkBoundResource<PastSessionDetailsModel,PastSessionDetailsModel>() {
                override fun shouldUseLocalDb(): Boolean = false

                override fun createCall(): LiveData<ApiResponse<PastSessionDetailsModel>> {
                    return RetrofitLiveData(mWebService.getCustomerPastSessionDetails(sessionId))
                }

            }.asLiveData


    companion object : SingletonHolder<HomeRepository, Application>({ HomeRepository(it.applicationContext) })
}