package com.checkin.app.checkin.menu

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.data.db.ObjectBoxInstanceLiveData
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.session.models.TrendingDishModel
import com.checkin.app.checkin.utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode
import io.objectbox.Box

class MenuRepository private constructor(context: Context) {
    private val mMenuBox: Box<MenuModel> = AppDatabase.getMenuModel(context)
    private val mCartBox: Box<CartStatusModel> = AppDatabase.getCartStatusModel(context)
    private val mWebService: WebApiService = getApiService(context)

    fun getAvailableMenu(shopId: Long): LiveData<Resource<MenuModel>> {
        return object : NetworkBoundResource<MenuModel, MenuModel>() {
            override fun shouldUseLocalDb(): Boolean = true

            override fun loadFromDb(): LiveData<MenuModel> {
                return ObjectBoxInstanceLiveData(mMenuBox
                        .query().equal(MenuModel_.restaurantPk, shopId)
                        .build())
            }

            override fun createCall(): LiveData<ApiResponse<MenuModel>> {
                return RetrofitLiveData(mWebService.getAvailableMenu(shopId))
            }

            override fun saveCallResult(data: MenuModel?) {
                data?.let {
                    it.restaurantPk = shopId
                    mMenuBox.put(it)
                }
            }
        }.asLiveData
    }

    fun postActiveSessionOrders(orders: List<OrderedItemModel>): LiveData<Resource<List<NewOrderModel>>> {
        return object : NetworkBoundResource<List<NewOrderModel>, List<NewOrderModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<NewOrderModel>>> {
                return RetrofitLiveData(mWebService.postActiveSessionOrders(orders))
            }
        }.asLiveData
    }

    fun postManageSessionOrders(sessionPk: Long, orders: List<OrderedItemModel>): LiveData<Resource<List<NewOrderModel>>> {
        return object : NetworkBoundResource<List<NewOrderModel>, List<NewOrderModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<NewOrderModel>>> {
                return RetrofitLiveData(mWebService.postSessionManagerOrders(sessionPk, orders))
            }
        }.asLiveData
    }

    fun postScheduledSessionOrders(sessionPk: Long, orders: List<OrderedItemModel>): LiveData<Resource<List<NewOrderModel>>> {
        return object : NetworkBoundResource<List<NewOrderModel>, List<NewOrderModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<NewOrderModel>>> {
                return RetrofitLiveData(mWebService.postScheduledSessionOrders(sessionPk, orders))
            }
        }.asLiveData
    }

    fun updateScheduledSessionOrder(sessionPk: Long, orderId: Long, quantity: Int): LiveData<Resource<OrderedItemModel>> {
        val body = Converters.objectMapper.createObjectNode().apply {
            put("quantity", quantity)
        }
        return object : NetworkBoundResource<OrderedItemModel, OrderedItemModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<OrderedItemModel>> = RetrofitLiveData(mWebService.patchScheduledSessionOrder(sessionPk, orderId, body))
        }.asLiveData
    }

    fun removeScheduledSessionOrder(sessionPk: Long, orderId: Long): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> = RetrofitLiveData(mWebService.deleteScheduledSessionOrder(sessionPk, orderId))
        }.asLiveData
    }

    fun getRecommendedDishes(restaurantId: Long): LiveData<Resource<List<TrendingDishModel>>> {
        return object : NetworkBoundResource<List<TrendingDishModel>, List<TrendingDishModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<TrendingDishModel>>> {
                return RetrofitLiveData(mWebService.getRestaurantRecommendedItems(restaurantId))
            }
        }.asLiveData
    }

    val checkCartStatus: LiveData<Resource<CartStatusModel>>
        get() = object : NetworkBoundResource<CartStatusModel, CartStatusModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun loadFromDb(): LiveData<CartStatusModel> = ObjectBoxInstanceLiveData(mCartBox.query().build())

            override fun createCall(): LiveData<ApiResponse<CartStatusModel>> = RetrofitLiveData(mWebService.sessionCartStatus)

            override fun saveCallResult(data: CartStatusModel?) {
                data?.let {
                    mCartBox.removeAll()
                    mCartBox.put(it)
                }
            }
        }.asLiveData

    val cartDetails: LiveData<Resource<CartDetailModel>>
        get() = object : NetworkBoundResource<CartDetailModel, CartDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<CartDetailModel>> = RetrofitLiveData(mWebService.sessionCartDetail)
        }.asLiveData

    val cartBillData: LiveData<Resource<CartBillModel>>
        get() = object : NetworkBoundResource<CartBillModel, CartBillModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<CartBillModel>> = RetrofitLiveData(mWebService.sessionCartBillDetail)
        }.asLiveData

    companion object : SingletonHolder<MenuRepository, Application>({ MenuRepository(it.applicationContext) })
}