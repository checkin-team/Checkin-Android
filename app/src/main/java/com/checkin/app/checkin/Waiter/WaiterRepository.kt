package com.checkin.app.checkin.Waiter

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Waiter.Model.*
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.dbStore
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.session.models.*
import com.checkin.app.checkin.utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode
import io.objectbox.android.ObjectBoxLiveData

/**
 * Created by Shivansh Saini on 24/01/2019.
 */
class WaiterRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)
    private val tableBox by dbStore<RestaurantTableModel>()
    private val tableSessionBox by dbStore<TableSessionModel>()

    val waiterServedTables: LiveData<Resource<List<WaiterTableModel>>>
        get() = object : NetworkBoundResource<List<WaiterTableModel>, List<WaiterTableModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<WaiterTableModel>>> {
                return RetrofitLiveData(mWebService.waiterServedTables)
            }

            override fun saveCallResult(data: List<WaiterTableModel>?) {}
        }.asLiveData

    fun postSessionContact(sessionId: Long, data: SessionContactModel): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.postSessionContact(sessionId, data))
            }

            override fun saveCallResult(data: ObjectNode?) {}
        }.asLiveData
    }

    fun getSessionContacts(sessionId: Long): LiveData<Resource<List<SessionContactModel>>> {
        return object : NetworkBoundResource<List<SessionContactModel>, List<SessionContactModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<SessionContactModel>>> {
                return RetrofitLiveData(mWebService.getSessionContactList(sessionId))
            }

            override fun saveCallResult(data: List<SessionContactModel>?) {}
        }.asLiveData
    }

    fun getWaiterEventsForTable(sessionId: Long): LiveData<Resource<List<WaiterEventModel>>> {
        return object : NetworkBoundResource<List<WaiterEventModel>, List<WaiterEventModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<WaiterEventModel>>> {
                return RetrofitLiveData(mWebService.getWaiterSessionEvents(sessionId))
            }

            override fun saveCallResult(data: List<WaiterEventModel>?) {}
        }.asLiveData
    }

    fun getShopTables(shopId: Long): LiveData<Resource<List<RestaurantTableModel>>> {
        return object : NetworkBoundResource<List<RestaurantTableModel>, List<RestaurantTableModel>>() {
            override fun shouldUseLocalDb(): Boolean = true

            override fun loadFromDb(): LiveData<List<RestaurantTableModel>>? {
                return ObjectBoxLiveData(tableBox.query()
                        .equal(RestaurantTableModel_.restaurantPk, shopId)
                        .build())
            }

            override fun createCall(): LiveData<ApiResponse<List<RestaurantTableModel>>> = RetrofitLiveData(mWebService.getRestaurantTables(shopId))

            override fun saveCallResult(data: List<RestaurantTableModel>?) = data?.run {
                forEach {
                    it.restaurantPk = shopId
                }
                tableBox.put(this)
            } ?: Unit
        }.asLiveData
    }

    fun filterShopTables(shopId: Long, activeQuery: Boolean): LiveData<Resource<List<RestaurantTableModel>>> {
        return object : NetworkBoundResource<List<RestaurantTableModel>, List<RestaurantTableModel>>() {
            override fun shouldUseLocalDb(): Boolean = true

            override fun loadFromDb(): LiveData<List<RestaurantTableModel>>? {
                return ObjectBoxLiveData(tableBox.query()
                        .equal(RestaurantTableModel_.restaurantPk, shopId)
                        .apply {
                            if (activeQuery) notNull(RestaurantTableModel_.relTableSessionId)
                            else isNull(RestaurantTableModel_.relTableSessionId)
                        }
                        .build())
            }

            override fun createCall(): LiveData<ApiResponse<List<RestaurantTableModel>>> = RetrofitLiveData(mWebService.filterRestaurantTables(shopId, activeQuery))

            override fun saveCallResult(data: List<RestaurantTableModel>?) = data?.run {
                forEach {
                    val savedTable = tableBox.get(it.qrPk)
                    if (savedTable != null) it.unseenEventCount = savedTable.unseenEventCount
                    it.restaurantPk = shopId
                }
                tableBox.put(this)
            } ?: Unit

            override fun transformResult(networkResult: Resource<List<RestaurantTableModel>>, dbResult: Resource<List<RestaurantTableModel>>?): Resource<List<RestaurantTableModel>> {
                return dbResult?.data?.let { dbList ->
                    if (networkResult.data == null) dbResult
                    else {
                        val result = networkResult.data.map { targetTable ->
                            val savedTable = dbList.find { it == targetTable }
                                    ?: return@map targetTable
                            targetTable.restaurantPk = savedTable.restaurantPk
                            targetTable.unseenEventCount = savedTable.unseenEventCount
                            targetTable
                        }
                        Resource.cloneResource(networkResult, result)
                    }
                } ?: networkResult
            }
        }.asLiveData
    }

    fun newWaiterSession(requestJson: ObjectNode): LiveData<Resource<QRResultModel>> {
        return object : NetworkBoundResource<QRResultModel, QRResultModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<QRResultModel>> {
                return RetrofitLiveData(mWebService.postNewWaiterSession(requestJson))
            }

            override fun saveCallResult(data: QRResultModel?) {}
        }.asLiveData
    }

    fun changeOrderStatus(orderId: Long, data: ObjectNode): LiveData<Resource<OrderStatusModel>> {
        return object : NetworkBoundResource<OrderStatusModel, OrderStatusModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<OrderStatusModel>> {
                return RetrofitLiveData(mWebService.postChangeOrderStatus(orderId, data))
            }

            override fun saveCallResult(data: OrderStatusModel?) {}
        }.asLiveData
    }

    fun markEventDone(eventId: Long): LiveData<Resource<GenericDetailModel>> {
        return object : NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<GenericDetailModel>> {
                return RetrofitLiveData(mWebService.putSessionEventDone(eventId))
            }

            override fun saveCallResult(data: GenericDetailModel?) {}
        }.asLiveData
    }

    fun getWaiterStats(restaurantId: Long): LiveData<Resource<WaiterStatsModel>> {
        return object : NetworkBoundResource<WaiterStatsModel, WaiterStatsModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<WaiterStatsModel>> {
                return RetrofitLiveData(mWebService.getRestaurantWaiterStats(restaurantId))
            }

            override fun saveCallResult(data: WaiterStatsModel?) {}
        }.asLiveData
    }

    fun postSessionRequestCheckout(sessionId: Long, data: ObjectNode): LiveData<Resource<CheckoutStatusModel>> {
        return object : NetworkBoundResource<CheckoutStatusModel, CheckoutStatusModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<CheckoutStatusModel>> {
                return RetrofitLiveData(mWebService.postSessionRequestCheckout(sessionId, data))
            }

            override fun saveCallResult(data: CheckoutStatusModel?) {}
        }.asLiveData
    }

    fun postOrderListStatus(sessionId: Long, orders: List<OrderStatusModel>): LiveData<Resource<List<OrderStatusModel>>> {
        return object : NetworkBoundResource<List<OrderStatusModel>, List<OrderStatusModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<OrderStatusModel>>> {
                return RetrofitLiveData(mWebService.postChangeOrderStatusList(sessionId, orders))
            }

            override fun saveCallResult(data: List<OrderStatusModel>?) {}
        }.asLiveData
    }

    companion object : SingletonHolder<WaiterRepository, Application>({ WaiterRepository(it.applicationContext) })
}