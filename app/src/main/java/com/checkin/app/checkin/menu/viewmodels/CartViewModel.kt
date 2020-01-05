package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Data.AppDatabase
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.models.NewScheduledSessionModel
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.SessionBillModel
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.*

class CartViewModel(application: Application) : BaseViewModel(application) {
    private val menuRepository = MenuRepository.getInstance(application)

    private val mNewOrders = createNetworkLiveData<List<NewOrderModel>>()
    private val mUpdatedOrder = createNetworkLiveData<OrderedItemModel>()
    private val mDeletedOrder = createNetworkLiveData<ObjectNode>()
    private val mCartStatusData = createNetworkLiveData<CartStatusModel>()
    private val mCartBillData = createNetworkLiveData<CartBillModel>()

    private val mServerPendingData = createNetworkLiveData<Any>()
    private val mCurrentItem = MutableLiveData<OrderedItemModel>()

    private val mOrderedItems = MediatorLiveData<List<OrderedItemModel>>()

    var sessionPk: Long = 0
    lateinit var sessionType: SessionType

    val cartStatus: LiveData<Resource<CartStatusModel>> = mCartStatusData
    val cartBillData: LiveData<Resource<CartBillModel>> = mCartBillData
    val cartDetailData: MutableLiveData<Resource<CartDetailModel>> = MutableLiveData()
    val orderedItems: LiveData<List<OrderedItemModel>>
        get() {
            if (mOrderedItems.value == null) mOrderedItems.value = emptyList()
            return mOrderedItems
        }

    init {
        mServerPendingData.addSource(mNewOrders) { mServerPendingData.value = Resource.cloneResource(it, pass) }
        mServerPendingData.addSource(mUpdatedOrder) { mServerPendingData.value = Resource.cloneResource(it, pass) }
        mServerPendingData.addSource(mDeletedOrder) { mServerPendingData.value = Resource.cloneResource(it, pass) }
    }

    val serverPendingData: LiveData<Resource<Any>> = mServerPendingData

    val currentItem: LiveData<OrderedItemModel> = mCurrentItem

    val totalOrderedCount: LiveData<Int> = Transformations.map(mOrderedItems) { it.sumBy { it.quantity } }

    val orderedSubTotal: LiveData<Double> = Transformations.map(mOrderedItems) { it.sumByDouble { it.cost } }

    val pendingOrders: LiveData<List<OrderedItemModel>> = Transformations.map(mOrderedItems) { list ->
        list.filter { it.pk == 0L }
    }

    val itemOrderedCounts: LiveData<Map<Long, Int>>
        get() = Transformations.map(mOrderedItems) {
            it?.let {
                it.groupBy({ it.itemPk() }, { it.quantity }).mapValues { it.value.sum() }
            }
        }

    private fun <T> processServerResult(resource: Resource<T>): Resource<T> {
        if (resource.status == Resource.Status.SUCCESS) {
            updateCartFromServer(mCurrentItem.value!!)
            mCurrentItem.value = null
        }
        return resource
    }

    fun fetchCartOrders() {
        mOrderedItems.addSource(menuRepository.cartDetails) {
            it?.let { resource ->
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    mOrderedItems.value = resource.data.orderedItems.map {
                        val menuItem = AppDatabase.getMenuItemModel(null).get(it.item.pk) ?: it.item
                        OrderedItemModel(
                                it.longPk, menuItem, it.cost, it.quantity, it.typeIndex,
                                it.customizations.flatMap { it.customizationFields }.distinct(),
                                it.remarks, it.ordered
                        )
                    }
                    cartDetailData.value = resource
                }
            }
        }
    }

    fun fetchCartStatus() {
        mCartStatusData.addSource(menuRepository.checkCartStatus, mCartStatusData::setValue)
    }

    fun fetchCartBill() {
        mCartBillData.addSource(menuRepository.cartBillData, mCartBillData::setValue)
    }

    fun retryOrder() {
        mCurrentItem.value?.let { orderItem(it) }
    }

    fun orderItem(order: OrderedItemModel) {
        if (sessionType == SessionType.SCHEDULED && sessionPk != 0L)
            syncOrdersWithServer(order)
        else updateCart(order)
    }

    fun syncOrdersWithServer() {
        pendingOrders.value?.let { if (it.isNotEmpty()) postScheduledOrders(it) }
    }

    private fun syncOrdersWithServer(order: OrderedItemModel) {
        when {
            order.pk == 0L -> postScheduledOrder(order)
            order.quantity > 0 -> patchScheduledOrder(order)
            else -> deleteScheduledOrder(order)
        }
    }

    private fun postScheduledOrders(orders: List<OrderedItemModel>) {
        mNewOrders.addSource(menuRepository.postScheduledSessionOrders(sessionPk, orders)) {
            mNewOrders.value = it?.let {
                if (it.status == Resource.Status.SUCCESS && it.data != null) updateCartFromServer(it.data)
                it
            } ?: it
        }
    }

    private fun postScheduledOrder(order: OrderedItemModel) {
        mCurrentItem.value = order
        mNewOrders.addSource(menuRepository.postScheduledSessionOrders(sessionPk, listOf(order))) {
            if (it?.status == Resource.Status.SUCCESS && it.data != null) {
                mCurrentItem.value = mCurrentItem.value?.updatePk(it.data[0].pk)
            }
            mNewOrders.value = processServerResult(it)
        }
    }

    private fun patchScheduledOrder(order: OrderedItemModel) {
        mCurrentItem.value = order
        mUpdatedOrder.addSource(menuRepository.updateScheduledSessionOrder(sessionPk, order.pk, order.quantity)) {
            mUpdatedOrder.value = processServerResult(it)
        }
    }

    private fun deleteScheduledOrder(order: OrderedItemModel) {
        mCurrentItem.value = order
        mDeletedOrder.addSource(menuRepository.removeScheduledSessionOrder(sessionPk, order.pk)) {
            mDeletedOrder.value = processServerResult(it)
        }
    }

    private fun updateCartFromServer(order: OrderedItemModel) {
        val orderedItems = mOrderedItems.value?.toMutableList() ?: mutableListOf()
        val index = orderedItems.indexOfFirst { it.pk == order.pk }
        if (index != -1) {
            orderedItems[index] = orderedItems[index].updateQuantity(order.quantity)
            if (orderedItems[index].quantity == 0) orderedItems.removeAt(index)
        } else orderedItems.add(order)
        mOrderedItems.value = orderedItems
    }

    private fun updateCartFromServer(items: List<NewOrderModel>) {
        val orderedItems = mOrderedItems.value?.toMutableList() ?: mutableListOf()
        items.forEach { newOrder ->
            val index = orderedItems.indexOfFirst {
                newOrder.item == it.itemPk() && newOrder.typeIndex == it.typeIndex && newOrder.customizations == it.customizations()
            }
            if (index != -1) {
                orderedItems[index] = orderedItems[index].updatePk(newOrder.pk).updateQuantity(newOrder.quantity)
                if (orderedItems[index].quantity == 0) orderedItems.removeAt(index)
            }
        }
        mOrderedItems.value = orderedItems
    }

    private fun updateCart(item: OrderedItemModel) {
        val orderedItems = mOrderedItems.value?.toMutableList() ?: mutableListOf()
        val index = orderedItems.indexOfFirst {
            it.equalsWithPk(item) || it.equalsWithoutPk(item)
        }
        if (index != -1) {
            if (item.quantity > 0) orderedItems[index] = item
            else orderedItems.removeAt(index)
        } else orderedItems.add(item)
        mOrderedItems.value = orderedItems
    }

    fun updateOrderedItem(item: MenuItemModel, count: Int): OrderedItemModel? {
        val items = mOrderedItems.value ?: emptyList()
        val cartCount = items.count { it.itemPk() == item.pk }
        var orderedItem = items.find { item.pk == it.itemPk() }
        if (cartCount == 0) return orderedItem
        if (item.isComplexItem && cartCount != count) {
            // Under the assumption that count > cartCount for complex item, always
            orderedItem = item.order(1)
        } else if (orderedItem != null && orderedItem.quantity != count) {
            orderedItem = orderedItem.updateQuantity(count)
        }
        return orderedItem
    }

    fun confirmOrder() {
        if (sessionPk == 0L)
            mNewOrders.addSource(menuRepository.postActiveSessionOrders(mOrderedItems.value!!)) { mNewOrders.setValue(it) }
        else
            mNewOrders.addSource(menuRepository.postManageSessionOrders(sessionPk, mOrderedItems.value!!)) { mNewOrders.setValue(it) }
    }

    fun updateCartDataWithNewSession(data: NewScheduledSessionModel) {
        cartDetailData.value = Resource.success(CartDetailModel(
                data.pk, emptyList(),
                ScheduledSessionDetailModel(data.countPeople
                        ?: 1, data.plannedDatetime, data.remarks, null, null, Calendar.getInstance().time),
                RestaurantLocationModel(data.restaurantId, "", null, null, null),
                SessionBillModel()
        ))
    }

    override fun updateResults() {
    }
}

enum class SessionType {
    ACTIVE, SCHEDULED
}
