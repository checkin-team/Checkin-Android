package com.checkin.app.checkin.menu.listeners

import com.checkin.app.checkin.menu.models.MenuItemModel

interface MenuItemInteraction {
    fun onMenuItemAdded(item: MenuItemModel): Boolean
    fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean
    fun onMenuItemShowInfo(item: MenuItemModel)
    fun getItemOrderedCount(item: MenuItemModel): Int
}