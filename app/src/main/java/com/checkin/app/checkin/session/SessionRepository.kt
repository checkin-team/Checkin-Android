package com.checkin.app.checkin.session

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
import com.checkin.app.checkin.home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.manager.models.ManagerSessionEventModel
import com.checkin.app.checkin.session.models.*
import com.checkin.app.checkin.user.models.UserTransactionBriefModel
import com.checkin.app.checkin.user.models.UserTransactionDetailsModel
import com.checkin.app.checkin.utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode

class SessionRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    val activeSessionLiveStatus: LiveData<Resource<ActiveLiveSessionDetailModel>>
        get() = object : NetworkBoundResource<ActiveLiveSessionDetailModel, ActiveLiveSessionDetailModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ActiveLiveSessionDetailModel>> {
                return RetrofitLiveData(mWebService.activeSessionLiveStatus)
            }
        }.asLiveData

    val activeSessionCheck: LiveData<Resource<SessionBasicModel>>
        get() = object : NetworkBoundResource<SessionBasicModel, SessionBasicModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<SessionBasicModel>> {
                return RetrofitLiveData(mWebService.activeSessionCheck)
            }
        }.asLiveData

    val allPromoCodes: LiveData<Resource<List<PromoDetailModel>>>
        get() =  object : NetworkBoundResource<List<PromoDetailModel>, List<PromoDetailModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<PromoDetailModel>>> {
                return RetrofitLiveData(mWebService.promoCodes)
            }
        }.asLiveData

    fun getRestaurantPromoCodes(restaurantId: Long, forActive: Boolean?): LiveData<Resource<List<PromoDetailModel>>> {
        val filterChoice = when (forActive) {
            true -> "rest.active"
            false -> "rest.cbyg"
            else -> "rest.all"
        }
        return object : NetworkBoundResource<List<PromoDetailModel>, List<PromoDetailModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall() = RetrofitLiveData(mWebService.getRestaurantActivePromos(restaurantId, filterChoice))
        }.asLiveData
    }

    fun newCustomerSession(data: ObjectNode): LiveData<Resource<QRResultModel>> {
        return object : NetworkBoundResource<QRResultModel, QRResultModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<QRResultModel>> {
                return RetrofitLiveData(mWebService.postNewCustomerSession(data))
            }
        }.asLiveData
    }

    val clearCustomerCart: LiveData<Resource<ObjectNode>>
        get() = object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteCustomerCart)
            }
        }.asLiveData

    fun newScheduledSession(data: NewScheduledSessionModel): LiveData<Resource<NewScheduledSessionModel>> {
        return object : NetworkBoundResource<NewScheduledSessionModel, NewScheduledSessionModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<NewScheduledSessionModel>> {
                return RetrofitLiveData(mWebService.postNewScheduledSession(data))
            }
        }.asLiveData
    }

    fun getSessionBriefDetail(sessionPk: Long): LiveData<Resource<SessionBriefModel>> {
        return object : NetworkBoundResource<SessionBriefModel, SessionBriefModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<SessionBriefModel>> {
                return RetrofitLiveData(mWebService.getSessionBriefDetail(sessionPk))
            }
        }.asLiveData
    }

    fun getSessionOrders(sessionId: Long): LiveData<Resource<List<SessionOrderedItemModel>>> {
        return object : NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<SessionOrderedItemModel>>> {
                return RetrofitLiveData(mWebService.getSessionOrders(sessionId))
            }
        }.asLiveData
    }

    fun getSessionEvents(sessionId: Long): LiveData<Resource<List<ManagerSessionEventModel>>> {
        return object : NetworkBoundResource<List<ManagerSessionEventModel>, List<ManagerSessionEventModel>>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<ManagerSessionEventModel>>> {
                return RetrofitLiveData(mWebService.getManagerSessionEvents(sessionId))
            }
        }.asLiveData
    }


    fun getUserSessionDetail(sessionId: Long): LiveData<Resource<UserTransactionDetailsModel>> {
        return object : NetworkBoundResource<UserTransactionDetailsModel, UserTransactionDetailsModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<UserTransactionDetailsModel>> {
                return RetrofitLiveData(mWebService.getUserSessionDetailById(sessionId))
            }
        }.asLiveData
    }

    fun getUserSessionBriefDetail(sessionPk: Long): LiveData<Resource<UserTransactionBriefModel>> {
        return object : NetworkBoundResource<UserTransactionBriefModel, UserTransactionBriefModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<UserTransactionBriefModel>> {
                return RetrofitLiveData(mWebService.getUserSessionBrief(sessionPk))
            }
        }.asLiveData
    }

    fun cancelSessionJoinRequest(): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteCustomerSessionJoinRequest())
            }
        }.asLiveData
    }

    fun getScheduledSessions(): LiveData<Resource<List<ScheduledLiveSessionDetailModel>>> {
        return object : NetworkBoundResource<List<ScheduledLiveSessionDetailModel>, List<ScheduledLiveSessionDetailModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall() = RetrofitLiveData(mWebService.customerScheduledSessions)
        }.asLiveData
    }

    companion object : SingletonHolder<SessionRepository, Application>({ SessionRepository(it.applicationContext) })
}
