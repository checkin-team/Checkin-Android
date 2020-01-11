package com.checkin.app.checkin.session

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.*
import com.checkin.app.checkin.home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.User.bills.UserTransactionBriefModel
import com.checkin.app.checkin.User.bills.UserTransactionDetailsModel
import com.checkin.app.checkin.Utility.SingletonHolder
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.ManagerSessionEventModel
import com.checkin.app.checkin.session.models.*
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

            override fun saveCallResult(data: ActiveLiveSessionDetailModel) {

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

            override fun saveCallResult(data: SessionBasicModel) {

            }
        }.asLiveData

    val availablePromoCodes: LiveData<Resource<List<PromoDetailModel>>>
        get() =  object : NetworkBoundResource<List<PromoDetailModel>, List<PromoDetailModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<PromoDetailModel>>> {
                return RetrofitLiveData<List<PromoDetailModel>>(mWebService.promoCodes)
            }

            override fun saveCallResult(data: List<PromoDetailModel>) {}
        }.asLiveData

    fun newCustomerSession(data: ObjectNode): LiveData<Resource<QRResultModel>> {
        return object : NetworkBoundResource<QRResultModel, QRResultModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<QRResultModel>> {
                return RetrofitLiveData(mWebService.postNewCustomerSession(data))
            }

            override fun saveCallResult(data: QRResultModel) {}
        }.asLiveData
    }

    val clearCustomerCart: LiveData<Resource<ObjectNode>>
        get() = object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteCustomerCart)
            }

            override fun saveCallResult(data: ObjectNode) {
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

            override fun saveCallResult(data: NewScheduledSessionModel) {}
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

            override fun saveCallResult(data: SessionBriefModel) {

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

            override fun saveCallResult(data: List<SessionOrderedItemModel>) {}
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

            override fun saveCallResult(data: List<ManagerSessionEventModel>) {}
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

            override fun saveCallResult(data: UserTransactionDetailsModel) {

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

            override fun saveCallResult(data: UserTransactionBriefModel) {

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

            override fun saveCallResult(data: ObjectNode) {
                //saveCallResult code
            }
        }.asLiveData
    }

    fun getScheduledSessions(): LiveData<Resource<List<ScheduledLiveSessionDetailModel>>> {
        return object : NetworkBoundResource<List<ScheduledLiveSessionDetailModel>, List<ScheduledLiveSessionDetailModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall() = RetrofitLiveData(mWebService.customerScheduledSessions)

            override fun saveCallResult(data: List<ScheduledLiveSessionDetailModel>?) {
            }

        }.asLiveData
    }

    companion object : SingletonHolder<SessionRepository, Application>({ SessionRepository(it.applicationContext) })
}
