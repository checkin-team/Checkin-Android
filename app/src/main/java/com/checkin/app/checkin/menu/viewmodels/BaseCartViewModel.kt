package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.menu.models.OrderedItemModel

abstract class BaseCartViewModel(application: Application) : BaseViewModel(application) {
    protected val menuRepository = MenuRepository.getInstance(application)

    protected val mOrderedItems = MediatorLiveData<List<OrderedItemModel>>()

    var sessionPk: Long = 0

    val orderedItems: LiveData<List<OrderedItemModel>>
        get() {
            if (mOrderedItems.value == null) mOrderedItems.value = emptyList()
            return mOrderedItems
        }

    val totalOrderedCount: LiveData<Int> = Transformations.map(mOrderedItems) { it.sumBy { it.quantity } }

    val orderedSubTotal: LiveData<Double> = Transformations.map(mOrderedItems) { it.sumByDouble { it.cost } }

    val itemOrderedCounts: LiveData<Map<Long, Int>> = Transformations.map(mOrderedItems) {
        it?.let {
            it.groupBy({ it.itemPk() }, { it.quantity }).mapValues { it.value.sum() }
        }
    }

    open fun orderItem(order: OrderedItemModel) {
        updateCart(order)
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
}