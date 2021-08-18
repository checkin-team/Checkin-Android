package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.CatalogRepository
import com.checkin.app.checkin.manager.models.*
import com.checkin.app.checkin.utility.SourceMappedLiveData

class CatalogViewModel(application: Application) : BaseViewModel(application) {
    private val mCatalogRepository = CatalogRepository.getInstance(application)
    private val mMenuGroupsList = createNetworkLiveData<List<MenuGroupModel>>()
    private val mMenu = createNetworkLiveData<CatalogMenuModel>()
    private val mMenuGroup = createNetworkLiveData<MenuGroupModel>()
    private val mMenuItem = createNetworkLiveData<GroupMenuItemModel>()
    val mVariantList = MutableLiveData<ArrayList<ItemVariantModel>>(ArrayList<ItemVariantModel>())
    val mCustomizationList = MutableLiveData<ArrayList<ItemCustomizationModel>>(ArrayList<ItemCustomizationModel>())
    var listOfTypes = ArrayList<String>()
    var listOfPrice = ArrayList<String>()
    var groupPk: Long = 0L
    var shopPk: Long = 0L

    fun createMenu(menu: CatalogMenuModel) {
        mMenu.addSource(mCatalogRepository.createMenu(shopPk, menu), mMenu::setValue)
    }

    fun createItem(item: GroupMenuItemModel) {
        mMenuItem.addSource(mCatalogRepository.createMenuItem(groupPk!!, item), mMenuItem::setValue)
    }

    fun getMenu(): LiveData<Resource<CatalogMenuModel>> {
        return mMenu
    }

    fun createMenuGroup(menuGroup: MenuGroupModel) {
        mMenuGroup.addSource(mCatalogRepository.createMenuGroup(shopPk, menuGroup), mMenuGroup::setValue)
    }

    fun getMenuGroup(): LiveData<Resource<MenuGroupModel>> {
        return mMenuGroup
    }

    fun getMenuItem() {

    }

    fun getGroupsList() {
        mMenuGroupsList.addSource(mCatalogRepository.getMenuGroups(shopPk), mMenuGroupsList::setValue)
    }

    fun getGroupNames(): LiveData<List<String>> {
        return Transformations.map(mMenuGroupsList) { input ->
            if (input != null && input.data != null && input.status == Resource.Status.SUCCESS) {
                val list = ArrayList<String>()
                input.data?.forEach {
                    list.add(it.name)
                }
                return@map list
            }
            return@map null
        }
    }

    fun setGroupPk(position: Int) {
        groupPk = mMenuGroupsList.value?.data?.get(position)?.pk?.toLong() ?: 0L
    }





























    override fun updateResults() {
        TODO("Not yet implemented")
    }


}