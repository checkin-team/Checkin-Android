package com.checkin.app.checkin.menu.controllers

import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.menu.holders.OnItemInteractionListener
import com.checkin.app.checkin.menu.holders.userMenuItemModelHolder
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController

class UserMenuItemController(
        val listener: OnItemInteractionListener?
) : BaseEpoxyController() {
    var itemList: List<MenuItemModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var orderedCountMap: Map<Long, Int>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        itemList?.let {
            it.forEach {
                userMenuItemModelHolder {
                    id(it.pk)
                    menuItem(it)
                    orderedCountMap?.also { map -> itemOrderedCount(map[it.pk] ?: 0) }
                    listener?.also { listener(it) }
                }
            }
        }
    }
}