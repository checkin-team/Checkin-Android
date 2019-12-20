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
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.CartBillModel
import com.checkin.app.checkin.menu.models.CartDetailModel
import com.checkin.app.checkin.menu.models.CartStatusModel
import com.checkin.app.checkin.menu.models.NewOrderModel
import com.fasterxml.jackson.databind.node.ObjectNode

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

    val totalOrderedCount: LiveData<Int>
        get() = Transformations.map(mOrderedItems) { input ->
            var res = 0
            for (item in input)
                res += item.quantity
            res
        }

    val orderedSubTotal: LiveData<Double>
        get() = Transformations.map(mOrderedItems) { input ->
            var res = 0.0
            for (item in input)
                res += item.cost
            res
        }

    val pendingOrders: LiveData<List<OrderedItemModel>> = Transformations.map(mOrderedItems) { list ->
        list.filter { it.pk == 0L }
    }

    val itemOrderedCounts: LiveData<Map<Long, Int>>
        get() = Transformations.map(mOrderedItems) {
            it?.let {
                it.groupBy({ it.itemModel.pk }, { it.quantity }).mapValues { it.value.sum() }
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
                        OrderedItemModel(menuItem, it.quantity, it.typeIndex).apply {
                            pk = it.longPk
                            cost = it.cost
                            selectedFields = it.customizations.flatMap { it.customizationFields }.distinct()
                            remarks = it.remarks
                        }
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

    fun orderItem(order: OrderedItemModel) {
        updateCart(order)
        if (sessionType == SessionType.SCHEDULED && sessionPk != 0L)
            syncOrdersWithServer(order)
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
            orderedItems[index].quantity = order.quantity
            if (orderedItems[index].quantity == 0) orderedItems.removeAt(index)
        }
        mOrderedItems.value = orderedItems
    }

    private fun updateCartFromServer(items: List<NewOrderModel>) {
        val orderedItems = mOrderedItems.value?.toMutableList() ?: mutableListOf()
        items.forEach { newOrder ->
            val index = orderedItems.indexOfFirst {
                newOrder.item == it.item && newOrder.customizations == it.selectedFields.map { it.pk }
            }
            if (index != -1) {
                orderedItems[index].quantity = newOrder.quantity
                if (orderedItems[index].quantity == 0) orderedItems.removeAt(index)
            }
        }
        mOrderedItems.value = orderedItems
    }

    private fun updateCart(item: OrderedItemModel) {
        val orderedItems = mOrderedItems.value?.toMutableList() ?: mutableListOf()
        val index = orderedItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 0) {
                item.quantity = orderedItems[index].quantity + item.changeCount
                orderedItems[index] = item
            } else orderedItems.removeAt(index)
        } else {
            if (item.itemModel.isComplexItem)
                item.quantity = item.changeCount
            orderedItems.add(item)
        }
        mOrderedItems.value = orderedItems
    }

    fun updateOrderedItem(item: MenuItemModel, count: Int): OrderedItemModel? {
        val items = mOrderedItems.value
        var orderedItem: OrderedItemModel? = null
        if (items == null) {
            return orderedItem
        }
        var cartCount = 0
        for (listItem in items) {
            if (item == listItem.itemModel) {
                cartCount += listItem.quantity
                if (!item.isComplexItem) {
                    try {
                        orderedItem = listItem.clone()
                    } catch (e: CloneNotSupportedException) {
                    }

                    break
                }
            }
        }
        if (cartCount == 0) {
            return orderedItem
        }
        if (item.isComplexItem && cartCount != count) {
            orderedItem = item.order(1)
        } else if (orderedItem != null && orderedItem.quantity != count) {
            orderedItem.quantity = count
        }
        return orderedItem
    }

    fun confirmOrder() {
        if (sessionPk == 0L)
            mNewOrders.addSource(menuRepository.postActiveSessionOrders(mOrderedItems.value!!)) { mNewOrders.setValue(it) }
        else
            mNewOrders.addSource(menuRepository.postManageSessionOrders(sessionPk, mOrderedItems.value!!)) { mNewOrders.setValue(it) }
    }

    override fun updateResults() {
    }
}

enum class SessionType {
    ACTIVE, SCHEDULED
}
