package com.checkin.app.checkin.data.db

import android.content.Context
import com.checkin.app.checkin.MyObjectBox
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.restaurant.models.RestaurantBriefModel
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

object AppDatabase {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        if (!::store.isInitialized)
            store = MyObjectBox.builder()
                    .androidContext(context.applicationContext)
                    .buildDefault()
    }

    inline fun <reified T> boxFor() = store.boxFor<T>()

    @JvmStatic
    fun getMenuItemCustomizationFieldModel(): Box<ItemCustomizationFieldModel> = store.boxFor()

    @JvmStatic
    fun getMenuItemCustomizationGroupModel(): Box<ItemCustomizationGroupModel> = store.boxFor()

    @JvmStatic
    fun getMenuItemModel(): Box<MenuItemModel> = store.boxFor()

    @JvmStatic
    fun getMenuGroupModel(): Box<MenuGroupModel> = store.boxFor()

    @JvmStatic
    fun getMenuModel(): Box<MenuModel> = store.boxFor()

    @JvmStatic
    fun getRestaurantBriefModel(): Box<RestaurantBriefModel> = store.boxFor()
}
