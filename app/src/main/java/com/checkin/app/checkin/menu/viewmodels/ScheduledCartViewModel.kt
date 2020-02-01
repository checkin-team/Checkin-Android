package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.models.NewScheduledSessionModel
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.SessionBillModel
import com.checkin.app.checkin.utility.pass
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.*

class ScheduledCartViewModel(application: Application) : BaseCartViewModel(application) {
    private val mNewOrders = createNetworkLiveData<List<NewOrderModel>>()
    private val mUpdatedOrder = createNetworkLiveData<OrderedItemModel>()
    private val mDeletedOrder = createNetworkLiveData<ObjectNode>()
    private val mCartStatusData = createNetworkLiveData<CartStatusModel>()
    private val mCartBillData = createNetworkLiveData<CartBillModel>()

    private val mServerPendingData = createNetworkLiveData<Any>()
    private val mCurrentItem = MutableLiveData<OrderedItemModel>()

    val cartStatus: LiveData<Resource<CartStatusModel>> = mCartStatusData
    val cartBillData: LiveData<Resource<CartBillModel>> = mCartBillData
    val cartDetailData: MutableLiveData<Resource<CartDetailModel>> = MutableLiveData()

    init {
        mServerPendingData.addSource(mNewOrders) { mServerPendingData.value = Resource.cloneResource(it, pass) }
        mServerPendingData.addSource(mUpdatedOrder) { mServerPendingData.value = Resource.cloneResource(it, pass) }
        mServerPendingData.addSource(mDeletedOrder) { mServerPendingData.value = Resource.cloneResource(it, pass) }
    }

    val serverPendingData: LiveData<Resource<Any>> = mServerPendingData

    val pendingOrders: LiveData<List<OrderedItemModel>> = Transformations.map(mOrderedItems) { list ->
        list.filter { it.pk == 0L }
    }

    private fun <T> processServerResult(resource: Resource<T>): Resource<T> {
        if (resource.status == Resource.Status.SUCCESS) {
            mCurrentItem.value?.let {
                updateCartFromServer(it)
                mCurrentItem.value = null
            }
        }
        return resource
    }

    fun fetchCartOrders() {
        mOrderedItems.addSource(menuRepository.cartDetails) {
            it?.let { resource ->
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    mOrderedItems.value = resource.data.orderedItems.map {
                        val menuItem = AppDatabase.getMenuItemModel(null).get(it.item.pk)
                                ?: return@addSource
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

    override fun orderItem(order: OrderedItemModel) {
        if (sessionPk != 0L) syncOrdersWithServer(order)
        else super.orderItem(order)
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
