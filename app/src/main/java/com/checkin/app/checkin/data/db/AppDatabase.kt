package com.checkin.app.checkin.data.db

import android.content.Context
import com.checkin.app.checkin.MyObjectBox
import com.checkin.app.checkin.accounts.AccountModel
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.restaurant.models.RestaurantBriefModel
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

object AppDatabase {
    private lateinit var store: BoxStore

    fun init(context: Context) {
        store = MyObjectBox.builder().androidContext(context).buildDefault()
    }

    @JvmStatic
    fun getAccountModel(): Box<AccountModel> = store.boxFor()

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

    @JvmStatic
    fun getCartStatusModel(): Box<CartStatusModel> = store.boxFor()

    @JvmStatic
    fun getUPICollectPaymentOptionModel(): Box<UPICollectPaymentOptionModel> = store.boxFor()

    @JvmStatic
    fun getUPIPushPaymentOptionModel(): Box<UPIPushPaymentOptionModel> = store.boxFor()
}
