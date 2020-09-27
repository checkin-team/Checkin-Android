package com.checkin.app.checkin.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode
import retrofit2.Call

class AuthRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)

    fun authenticate(credentials: AuthRequestModel): LiveData<Resource<AuthResultModel>> {
        return object : NetworkBoundResource<AuthResultModel, AuthResultModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<AuthResultModel>> = RetrofitLiveData(mWebService.postAuthenticate(credentials))

            override fun saveCallResult(data: AuthResultModel?) {}
        }.asLiveData
    }

    fun postDeviceToken(token: String?): Call<ObjectNode> {
        val data = objectMapper.createObjectNode()
        data.put("device_token", token)
        return mWebService.postFCMToken(data)
    }

    companion object : SingletonHolder<AuthRepository, Application>({ AuthRepository(it.applicationContext) })
}