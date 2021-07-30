package com.checkin.app.checkin.manager

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.MenuGroupModel
import com.checkin.app.checkin.manager.models.CatalogMenuModel
import com.checkin.app.checkin.manager.models.GroupMenuItemModel
import com.checkin.app.checkin.utility.SingletonHolder
import java.security.acl.Group

class CatalogRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    fun createMenu(restaurantId: Long, menu: CatalogMenuModel): LiveData<Resource<CatalogMenuModel>> {
        return object : NetworkBoundResource<CatalogMenuModel, CatalogMenuModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<CatalogMenuModel>> {
                return RetrofitLiveData(mWebService.postRestaurantMenu(restaurantId, menu))
            }
        }.asLiveData
    }

    fun createMenuGroup(restaurantId: Long, menuGroup : MenuGroupModel): LiveData<Resource<MenuGroupModel>> {
        return object : NetworkBoundResource<MenuGroupModel, MenuGroupModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<MenuGroupModel>> {
                return RetrofitLiveData(mWebService.postMenuGroup(restaurantId, menuGroup))
            }
        }.asLiveData

    }

    fun getMenuGroups(restaurantId: Long): LiveData<Resource<List<MenuGroupModel>>> {
        return object : NetworkBoundResource<List<MenuGroupModel>, List<MenuGroupModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<MenuGroupModel>>> {
                return RetrofitLiveData(mWebService.getMenuGroups(restaurantId))
            }
        }.asLiveData

    }

    fun createMenuItem(groupId: Long, menuItem : GroupMenuItemModel): LiveData<Resource<GroupMenuItemModel>> {
        return object : NetworkBoundResource<GroupMenuItemModel, GroupMenuItemModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<GroupMenuItemModel>> {
                return RetrofitLiveData(mWebService.postGroupMenuItem(groupId, menuItem))
            }
        }.asLiveData

    }

    companion object : SingletonHolder<CatalogRepository, Application>({ CatalogRepository(it.applicationContext) })
}