package com.checkin.app.checkin.Data

import android.content.Context
import com.checkin.app.checkin.Menu.Model.*
import com.checkin.app.checkin.MyObjectBox
import com.checkin.app.checkin.Shop.RestaurantLocationModel
import com.checkin.app.checkin.menu.models.CartStatusModel
import io.objectbox.Box
import io.objectbox.BoxStore

object AppDatabase {
    private var mBoxStore: BoxStore? = null

    private fun getBoxStore(context: Context?): BoxStore? {
        if (mBoxStore == null) {
            synchronized(AppDatabase::class.java) {
                if (mBoxStore == null) {
                    mBoxStore = MyObjectBox.builder().androidContext(context!!).buildDefault()
                }
            }
        }
        return mBoxStore
    }

    @JvmStatic
    fun getMenuItemCustomizationFieldModel(context: Context?): Box<ItemCustomizationFieldModel> {
        return getBoxStore(context)!!.boxFor(ItemCustomizationFieldModel::class.java)
    }

    @JvmStatic
    fun getMenuItemCustomizationGroupModel(context: Context?): Box<ItemCustomizationGroupModel> {
        return getBoxStore(context)!!.boxFor(ItemCustomizationGroupModel::class.java)
    }

    @JvmStatic
    fun getMenuItemModel(context: Context?): Box<MenuItemModel> {
        return getBoxStore(context)!!.boxFor(MenuItemModel::class.java)
    }

    @JvmStatic
    fun getMenuGroupModel(context: Context?): Box<MenuGroupModel> {
        return getBoxStore(context)!!.boxFor(MenuGroupModel::class.java)
    }

    @JvmStatic
    fun getMenuModel(context: Context?): Box<MenuModel> {
        return getBoxStore(context)!!.boxFor(MenuModel::class.java)
    }

    @JvmStatic
    fun getRestaurantLocationModel(context: Context?): Box<RestaurantLocationModel> = getBoxStore(context)!!.boxFor(RestaurantLocationModel::class.java)

    @JvmStatic
    fun getCartStatusModel(context: Context?): Box<CartStatusModel> = getBoxStore(context)!!.boxFor(CartStatusModel::class.java)

    fun init(context: Context) {
        getBoxStore(context)
    }
}