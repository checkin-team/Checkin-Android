package com.checkin.app.checkin.menu.controllers

import com.checkin.app.checkin.menu.holders.CartOrderInteraction
import com.checkin.app.checkin.menu.holders.cartOrderedItemModelHolder
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController

class CartOrderedItemController(
        val listener: CartOrderInteraction
) : BaseEpoxyController() {
    var orderedItems: List<OrderedItemModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        orderedItems?.forEach {order ->
//            val order = it.clone()
            cartOrderedItemModelHolder {
                id(order.getUniqueId())
                orderedItem(order)
                listener(listener)
            }
        }
    }
}

fun OrderedItemModel.getUniqueId(): Long = if (pk != 0L) pk else hashCode().toLong()