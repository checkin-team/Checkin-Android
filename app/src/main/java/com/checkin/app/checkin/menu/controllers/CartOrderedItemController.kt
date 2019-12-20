package com.checkin.app.checkin.menu.controllers

import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.menu.holders.CartOrderInteraction
import com.checkin.app.checkin.menu.holders.cartOrderedItemModelHolder
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
        orderedItems?.forEach {
            cartOrderedItemModelHolder {
                id(getUniqueId(it))
                orderedItem(it)
                listener(listener)
            }
        }
    }

    private fun getUniqueId(orderedItemModel: OrderedItemModel) = if (orderedItemModel.pk != 0L) orderedItemModel.pk else orderedItemModel.hashCode().toLong()
}
