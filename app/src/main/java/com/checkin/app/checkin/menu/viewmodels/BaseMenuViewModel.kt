package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.Comparator

abstract class BaseMenuViewModel(application: Application) : BaseViewModel(application) {
    protected val mRepository: MenuRepository = MenuRepository.getInstance(application)

    protected val mMenuData = createNetworkLiveData<MenuModel>()
    private val mOriginalMenuGroups = createNetworkLiveData<List<MenuGroupModel>>()
    private val mMenuGroups = MediatorLiveData<Resource<List<MenuGroupModel>>>()
    private val mMenuItems =
            createNetworkLiveData<List<MenuItemModel>>()

    private val mCurrentItem = MutableLiveData<OrderedItemModel>()
    private val mFilteredString = MutableLiveData<String>()
    private val mSelectedCategory = MutableLiveData<String>()

    protected var shopPk: Long = 0

    val menuGroups: LiveData<Resource<List<MenuGroupModel>>> = mMenuGroups

    val filteredMenuItems: LiveData<Resource<List<MenuItemModel>>> = mMenuItems

    val currentItem: LiveData<OrderedItemModel> = mCurrentItem

    val itemCost: LiveData<Double> = Transformations.map(mCurrentItem) { orderedItem ->
        if (orderedItem != null) orderedItem.cost / orderedItem.quantity else 0.0
    }

    val selectedCategory: LiveData<String> = mSelectedCategory

    val originalMenuGroups: LiveData<Resource<List<MenuGroupModel>>> = mOriginalMenuGroups

    val groupName: LiveData<List<String>>
        get() = Transformations.map(mMenuGroups) { input ->
            input?.data?.let {
                val categories = ArrayList<String>()
                for (menuGroupModel in it) {
                    categories.add(menuGroupModel.name)
                }
                categories
            }
        }

    val filteredString: LiveData<String> = mFilteredString

    val categories: LiveData<List<String>>
        get() = Transformations.map(mOriginalMenuGroups) { input ->
            input?.data?.let {
                val categories = ArrayList<String>()
                var category = ""
                for (menuGroupModel in it) {
                    if (!category.contentEquals(menuGroupModel.category)) {
                        category = menuGroupModel.category
                        categories.add(category)
                    }
                }
                categories
            }
        }

    fun resetMenuGroups() {
        mSelectedCategory.value = null
        mMenuGroups.value = mOriginalMenuGroups.value
    }

    fun canOrder(): Boolean = mCurrentItem.value?.canOrder() ?: false

    fun resetItem() {
        mCurrentItem.value = null
    }

    fun newOrderedItem(item: MenuItemModel) {
        val orderedItem = item.order(1)
        mCurrentItem.value = orderedItem
    }

    fun sortMenuItems(low2high: Boolean) {
        mFilteredString.value = if (low2high) "Low - High" else "High - Low"
        val resourceLiveData = Transformations.map(mMenuData) { input ->
            if (input?.data == null)
                return@map null
            val items = mutableListOf<MenuItemModel>().apply {
                input.data.groups.map { addAll(it.items) }
            }
            if (items.size == 0)
                return@map Resource.errorNotFound<List<MenuItemModel>>("")
            items.sortWith(Comparator { o1, o2 ->
                val diff = (o1.typeCosts[0] - o2.typeCosts[0]).toInt()
                if (low2high) diff else -diff
            })
            Resource.cloneResource<List<MenuItemModel>, MenuModel>(input, items)
        }
        mMenuItems.addSource(resourceLiveData) {
            mMenuItems.removeSource(resourceLiveData)
            mMenuItems.setValue(it)
        }
    }

    fun clearFilters() {
        mFilteredString.value = null
        mMenuGroups.value = mOriginalMenuGroups.value
    }

    fun resetMenuItems() {
        mMenuItems.value = Resource.noRequest()
    }

    private suspend fun internalSearch(query: String) = withContext(viewModelScope.coroutineContext) {
        val resourceLiveData: LiveData<Resource<List<MenuItemModel>>> = Transformations.map(mMenuData) { input ->
            if (input?.data == null)
                return@map null
            val items = input.data.groups.flatMap { it.items.filter { it.name.contains(query, ignoreCase = true) } }
            if (items.isEmpty()) return@map Resource.errorNotFound<List<MenuItemModel>>("Dish not found.")
            Resource.cloneResource(input, items)
        }
        mMenuItems.addSource(resourceLiveData) {
            mMenuItems.removeSource(resourceLiveData)
            mMenuItems.value = it
        }
    }

    fun searchMenuItems(query: String) {
        viewModelScope.launch {
            mMenuItems.value = Resource.loading(null)
            delay(500)
            internalSearch(query)
        }
    }

    fun filterMenuGroups(availableMeal: MenuItemModel.AVAILABLE_MEAL) {
        when (availableMeal) {
            MenuItemModel.AVAILABLE_MEAL.BREAKFAST -> mFilteredString.setValue("Breakfast")
            MenuItemModel.AVAILABLE_MEAL.LUNCH -> mFilteredString.setValue("Lunch")
            MenuItemModel.AVAILABLE_MEAL.DINNER -> mFilteredString.setValue("Dinner")
            MenuItemModel.AVAILABLE_MEAL.NIGHTLIFE -> mFilteredString.value = "Nightlife"
        }
        val resourceLiveData: LiveData<Resource<List<MenuGroupModel>>> = Transformations.map(mOriginalMenuGroups) { listResource ->
            if (listResource?.data == null)
                return@map null
            val result = ArrayList<MenuGroupModel>()

            for (menuGroupModel in listResource.data) {
                val items = ArrayList<MenuItemModel>()
                for (menuItemModel in menuGroupModel.items) {
                    if (menuItemModel.availableMeals.contains(availableMeal)) {
                        items.add(menuItemModel)
                    }
                }
                val groupModel = try {
                    menuGroupModel.clone() as MenuGroupModel
                } catch (e: CloneNotSupportedException) {
                    e.printStackTrace()
                    menuGroupModel
                }

                if (items.size > 0) {
                    groupModel.items.clear()
                    groupModel.setCacheItems(items)
                    result.add(groupModel)
                }
            }
            Resource.cloneResource<List<MenuGroupModel>, List<MenuGroupModel>>(listResource, result)
        }
        mMenuGroups.addSource(resourceLiveData) { mMenuGroups.setValue(it) }
    }

    fun addItemCustomization(customizationField: ItemCustomizationFieldModel) {
        val item = mCurrentItem.value ?: return
        mCurrentItem.value = item.addCustomizationField(customizationField)
    }

    fun removeItemCustomization(customizationField: ItemCustomizationFieldModel) {
        val item = mCurrentItem.value ?: return
        mCurrentItem.value = item.removeCustomizationField(customizationField)
    }

    fun setSelectedType(selectedType: Int) {
        val item = mCurrentItem.value ?: return
        mCurrentItem.value = item.selectType(selectedType)
    }

    fun cancelItem() = mCurrentItem.value?.let {
        mCurrentItem.value = it.updateQuantity(0)
        resetItem()
    }

    fun setCurrentItem(item: OrderedItemModel) {
        mCurrentItem.value = item
    }

    open fun fetchAvailableMenu(shopId: Long) {
        shopPk = shopId
        mMenuData.addSource(mRepository.getAvailableMenu(shopId), mMenuData::setValue)
        resetMenuGroups()

        val resourceLiveData = Transformations.map(mMenuData) { menuModelResource ->
            mOriginalMenuGroups.value = Resource.loading(null)
            menuModelResource?.data?.let {
                val groups = it.groups
                groups.sortWith(Comparator { o1, o2 -> o1.category.compareTo(o2.category) })
                Resource.cloneResource(menuModelResource, groups)
            } ?: Resource.cloneResource<List<MenuGroupModel>, MenuModel>(menuModelResource, null)
        }
        mOriginalMenuGroups.addSource(resourceLiveData) { listResource ->
            mOriginalMenuGroups.value = listResource
            resetMenuGroups()
        }
    }

    fun filterMenuCategories(category: String) {
        mSelectedCategory.value?.let {
            if (it.equals(category, ignoreCase = true)) {
                resetMenuGroups()
                return
            }
        }
        mSelectedCategory.value = category

        val resourceLiveData: LiveData<Resource<List<MenuGroupModel>>> = Transformations.map(mOriginalMenuGroups) { listResource ->
            if (listResource?.data == null)
                return@map null
            val result = ArrayList<MenuGroupModel>()
            for (menuGroupModel in listResource.data) {
                val items = ArrayList<MenuItemModel>()
                if (menuGroupModel.category.equals(category, ignoreCase = true)) {
                    items.addAll(menuGroupModel.items)
                }

                if (items.size > 0) {
                    menuGroupModel.items.clear()
                    menuGroupModel.items = items
                    result.add(menuGroupModel)
                }
            }
            Resource.cloneResource(listResource, result)
        }
        mMenuGroups.addSource(resourceLiveData) {
            mMenuGroups.removeSource(resourceLiveData)
            mMenuGroups.value = it
        }
    }

    override fun updateResults() {
        mMenuData.addSource(mRepository.getAvailableMenu(shopPk), mMenuData::setValue)
    }
}