package com.checkin.app.checkin.manager

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.*
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.Utility.SingletonHolder
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.*
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.session.models.CheckoutStatusModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.fasterxml.jackson.databind.node.ObjectNode

class ManagerRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)

    fun getManagerStats(restaurantId: Long): LiveData<Resource<ManagerStatsModel>> {
        return object : NetworkBoundResource<ManagerStatsModel, ManagerStatsModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ManagerStatsModel>> {
                return RetrofitLiveData(mWebService.getRestaurantManagerStats(restaurantId))
            }

            override fun saveCallResult(data: ManagerStatsModel) {}
        }.asLiveData
    }

    fun manageSessionCheckout(sessionId: Long): LiveData<Resource<CheckoutStatusModel>> {
        return object : NetworkBoundResource<CheckoutStatusModel, CheckoutStatusModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<CheckoutStatusModel>> {
                return RetrofitLiveData(mWebService.putSessionCheckout(sessionId))
            }

            override fun saveCallResult(data: CheckoutStatusModel) {}
        }.asLiveData
    }

    fun getManagerSessionInvoice(sessionId: Long): LiveData<Resource<ManagerSessionInvoiceModel>> {
        return object : NetworkBoundResource<ManagerSessionInvoiceModel, ManagerSessionInvoiceModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ManagerSessionInvoiceModel>> {
                return RetrofitLiveData(mWebService.getManagerSessionInvoice(sessionId))
            }

            override fun saveCallResult(data: ManagerSessionInvoiceModel) {}
        }.asLiveData
    }

    fun putManageSessionBill(sessionId: Long, data: ObjectNode): LiveData<Resource<GenericDetailModel>> {
        return object : NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<GenericDetailModel>> {
                return RetrofitLiveData(mWebService.putManageSessionBill(sessionId, data))
            }

            override fun saveCallResult(data: GenericDetailModel) {}
        }.asLiveData
    }

    fun managerInitiateSession(requestJson: ObjectNode): LiveData<Resource<QRResultModel>> {
        return object : NetworkBoundResource<QRResultModel, QRResultModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<QRResultModel>> {
                return RetrofitLiveData(mWebService.postManageInitiateSession(requestJson))
            }

            override fun saveCallResult(data: QRResultModel) {
            }
        }.asLiveData
    }

    fun managerSessionSwitchTable(sessionId: Long, requestJson: ObjectNode): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.postTableSwitch(sessionId, requestJson))
            }

            override fun saveCallResult(data: ObjectNode) {}
        }.asLiveData
    }

    fun getRestaurantScheduledSession(restaurantId: Long): LiveData<Resource<List<ShopScheduledSessionModel>>> {
        return object : NetworkBoundResource<List<ShopScheduledSessionModel>, List<ShopScheduledSessionModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<ShopScheduledSessionModel>>> {
                return RetrofitLiveData(mWebService.getScheduledSessionsById(restaurantId))
            }

            override fun saveCallResult(data: List<ShopScheduledSessionModel>?) {
            }
        }.asLiveData
    }

    fun getScheduledSessionDetail(sessionId: Long): LiveData<Resource<ShopScheduledSessionDetailModel>> {
        return object : NetworkBoundResource<ShopScheduledSessionDetailModel, ShopScheduledSessionDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ShopScheduledSessionDetailModel>> {
                return RetrofitLiveData(mWebService.getManageScheduledSessionDetail(sessionId))
            }

            override fun saveCallResult(data: ShopScheduledSessionDetailModel?) {
            }

        }.asLiveData
    }

    fun acceptScheduledSession(sessionId: Long, data: PreparationTimeModel): LiveData<Resource<PreparationTimeModel>> {
        return object : NetworkBoundResource<PreparationTimeModel, PreparationTimeModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<PreparationTimeModel>> {
                return RetrofitLiveData(mWebService.patchManageScheduledSessionAccept(sessionId, data))
            }

            override fun saveCallResult(data: PreparationTimeModel?) {
            }

        }.asLiveData
    }

    fun doneScheduledSession(sessionId: Long): LiveData<Resource<ScheduledSessionDoneModel>> {
        return object : NetworkBoundResource<ScheduledSessionDoneModel, ScheduledSessionDoneModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ScheduledSessionDoneModel>> {
                return RetrofitLiveData(mWebService.patchManageScheduledSessionDone(sessionId, Converters.objectMapper.createObjectNode()))
            }

            override fun saveCallResult(data: ScheduledSessionDoneModel?) {
            }

        }.asLiveData
    }

    fun rejectScheduledSession(sessionId: Long, data: ScheduledSessionCancelModel): LiveData<Resource<GenericDetailModel>> {
        return object : NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<GenericDetailModel>> {
                return RetrofitLiveData(mWebService.postManageScheduledSessionReject(sessionId, data))
            }

            override fun saveCallResult(data: GenericDetailModel?) {
            }
        }.asLiveData
    }

    companion object : SingletonHolder<ManagerRepository, Application>({ ManagerRepository(it.applicationContext) })
}