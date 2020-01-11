package com.checkin.app.checkin.session.scheduled

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.*
import com.checkin.app.checkin.Utility.SingletonHolder
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.paytm.PaytmModel
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.NewScheduledSessionModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.session.models.SessionPromoModel
import com.fasterxml.jackson.databind.node.ObjectNode
import retrofit2.Call

class ScheduledSessionRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    fun editScheduledSession(sessionId: Long, data: NewScheduledSessionModel): LiveData<Resource<NewScheduledSessionModel>> {
        return object : NetworkBoundResource<NewScheduledSessionModel, NewScheduledSessionModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<NewScheduledSessionModel>> {
                return RetrofitLiveData(mWebService.patchScheduledSession(sessionId, data))
            }

            override fun saveCallResult(data: NewScheduledSessionModel) {}
        }.asLiveData
    }

    fun removePromoCode(sessionId: Long): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteAvailedPromoForScheduledSession(sessionId))
            }

            override fun saveCallResult(data: ObjectNode?) {
            }

        }.asLiveData
    }

    fun postAvailPromoCode(sessionId: Long, data: ObjectNode): LiveData<Resource<SessionPromoModel>> {
        return object : NetworkBoundResource<SessionPromoModel, SessionPromoModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<SessionPromoModel>> {
                return RetrofitLiveData(mWebService.availPromoForScheduledSession(sessionId, data))
            }

            override fun saveCallResult(data: SessionPromoModel?) {
            }

        }.asLiveData
    }

    fun getSessionAppliedPromo(sessionPk: Long): LiveData<Resource<SessionPromoModel>> {
        return object : NetworkBoundResource<SessionPromoModel, SessionPromoModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<SessionPromoModel>> {
                return RetrofitLiveData(mWebService.getAvailedPromoForScheduledSession(sessionPk))
            }

            override fun saveCallResult(data: SessionPromoModel?) {
            }

        }.asLiveData
    }

    fun syncPostPaytmCallback(data: ObjectNode?): Call<ObjectNode> {
        return mWebService.postPaytmCallback(data!!)
    }

    fun postPaytmDetailRequest(sessionId: Long): LiveData<Resource<PaytmModel>> {
        return object : NetworkBoundResource<PaytmModel, PaytmModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<PaytmModel>> {
                return RetrofitLiveData(mWebService.postPaytmRequestForScheduledSession(sessionId))
            }

            override fun saveCallResult(data: PaytmModel?) {
            }

        }.asLiveData
    }

    fun getCustomerScheduledSessionDetail(sessionId: Long): LiveData<Resource<CustomerScheduledSessionDetailModel>> {
        return object : NetworkBoundResource<CustomerScheduledSessionDetailModel, CustomerScheduledSessionDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<CustomerScheduledSessionDetailModel>> {
                return RetrofitLiveData(mWebService.getCustomerScheduledSessionDetail(sessionId))
            }

            override fun saveCallResult(data: CustomerScheduledSessionDetailModel?) {
            }

        }.asLiveData
    }

    fun deleteCustomerScheduledSession(sessionId: Long): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteCustomerScheduledSession(sessionId))
            }

            override fun saveCallResult(data: ObjectNode?) {
            }

        }.asLiveData
    }

    fun getScheduledSessionOrders(sessionId: Long): LiveData<Resource<List<SessionOrderedItemModel>>> {
        return object : NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<SessionOrderedItemModel>>> {
                return RetrofitLiveData(mWebService.getScheduledSessionOrders(sessionId))
            }

            override fun saveCallResult(data: List<SessionOrderedItemModel>?) {
            }

        }.asLiveData
    }

    companion object : SingletonHolder<ScheduledSessionRepository, Application>({ ScheduledSessionRepository(it.applicationContext) })
}