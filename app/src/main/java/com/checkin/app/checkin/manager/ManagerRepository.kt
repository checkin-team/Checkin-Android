package com.checkin.app.checkin.manager

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.*
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.session.models.CheckoutStatusModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.checkin.app.checkin.utility.SingletonHolder
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
        }.asLiveData
    }

    fun getRestaurantScheduledSession(restaurantId: Long): LiveData<Resource<List<ShopScheduledSessionModel>>> {
        return object : NetworkBoundResource<List<ShopScheduledSessionModel>, List<ShopScheduledSessionModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<ShopScheduledSessionModel>>> {
                return RetrofitLiveData(mWebService.getScheduledSessionsById(restaurantId))
            }
        }.asLiveData
    }

    fun getScheduledSessionDetail(sessionId: Long): LiveData<Resource<ShopScheduledSessionDetailModel>> {
        return object : NetworkBoundResource<ShopScheduledSessionDetailModel, ShopScheduledSessionDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ShopScheduledSessionDetailModel>> {
                return RetrofitLiveData(mWebService.getManageScheduledSessionDetail(sessionId))
            }

        }.asLiveData
    }

    fun acceptScheduledSession(sessionId: Long, data: PreparationTimeModel): LiveData<Resource<PreparationTimeModel>> {
        return object : NetworkBoundResource<PreparationTimeModel, PreparationTimeModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<PreparationTimeModel>> {
                return RetrofitLiveData(mWebService.patchManageScheduledSessionAccept(sessionId, data))
            }
        }.asLiveData
    }

    fun doneScheduledSession(sessionId: Long): LiveData<Resource<ScheduledSessionDoneModel>> {
        return object : NetworkBoundResource<ScheduledSessionDoneModel, ScheduledSessionDoneModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ScheduledSessionDoneModel>> {
                return RetrofitLiveData(mWebService.patchManageScheduledSessionDone(sessionId, Converters.objectMapper.createObjectNode()))
            }
        }.asLiveData
    }

    fun rejectScheduledSession(sessionId: Long, data: ScheduledSessionCancelModel): LiveData<Resource<GenericDetailModel>> {
        return object : NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<GenericDetailModel>> {
                return RetrofitLiveData(mWebService.postManageScheduledSessionReject(sessionId, data))
            }
        }.asLiveData
    }

    companion object : SingletonHolder<ManagerRepository, Application>({ ManagerRepository(it.applicationContext) })
}