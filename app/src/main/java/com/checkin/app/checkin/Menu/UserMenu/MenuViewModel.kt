package com.checkin.app.checkin.Menu.UserMenu

import android.app.Application
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.Model.*
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.session.activesession.ActiveSessionRepository
import com.checkin.app.checkin.session.models.TrendingDishModel
import com.fasterxml.jackson.databind.node.ArrayNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.Comparator

class MenuViewModel(application: Application) : BaseViewModel(application) {
    private val mHandler = Handler()
    private val mRepository: MenuRepository
    private val mActiveSessionRepository: ActiveSessionRepository

    private val mMenuData = createNetworkLiveData<MenuModel>()
    private var mOriginalMenuGroups = createNetworkLiveData<List<MenuGroupModel>>()
    private val mMenuGroups = MediatorLiveData<Resource<List<MenuGroupModel>>>()
    private val mMenuItems = createNetworkLiveData<List<MenuItemModel>>()
    private val mResultOrder = createNetworkLiveData<ArrayNode>()
    private val mTrendingData = createNetworkLiveData<List<TrendingDishModel>>()
    private val mRecommendedData = createNetworkLiveData<List<TrendingDishModel>>()

    private val mOrderedItems = MutableLiveData<List<OrderedItemModel>>()
    private val mCurrentItem = MutableLiveData<OrderedItemModel>()
    private val mFilteredString = MutableLiveData<String>()
    private val mSelectedCategory: MutableLiveData<String> = MutableLiveData()

    private var mRunnable: Runnable? = null
    private var mSessionPk: Long? = null
    private var mShopPk: Long = 0

    val menuGroups: LiveData<Resource<List<MenuGroupModel>>>
        get() = mMenuGroups

    val filteredMenuItems: LiveData<Resource<List<MenuItemModel>>>
        get() = mMenuItems

    val currentItem: LiveData<OrderedItemModel>
        get() = mCurrentItem

    val itemCost: LiveData<Double>
        get() = Transformations.map<OrderedItemModel, Double>(mCurrentItem) { orderedItem -> if (orderedItem != null) orderedItem.cost / orderedItem.quantity else 0.0 }

    val orderedItems: LiveData<List<OrderedItemModel>>
        get() {
            if (mOrderedItems.value == null)
                mOrderedItems.value = ArrayList()
            return mOrderedItems
        }

    val totalOrderedCount: LiveData<Int>
        get() = Transformations.map(mOrderedItems) { input ->
            var res = 0
            for (item in input)
                res += item.quantity
            res
        }

    val orderedSubTotal: LiveData<Double>
        get() = Transformations.map(mOrderedItems) { input ->
            var res = 0.0
            for (item in input)
                res += item.cost
            res
        }

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

    val serverOrderedItems: LiveData<Resource<ArrayNode>>
        get() = mResultOrder

    val filteredString: LiveData<String>
        get() = mFilteredString

    val recommendedItems: LiveData<Resource<List<TrendingDishModel>>>
        get() = mRecommendedData

    val menuTrendingItems: LiveData<Resource<List<TrendingDishModel>>>
        get() = mTrendingData

    init {
        mRepository = MenuRepository.getInstance(application)
        mActiveSessionRepository = ActiveSessionRepository.getInstance(application)
    }

    override fun updateResults() {
        mMenuData.addSource(mRepository.getAvailableMenu(mShopPk), { mMenuData.setValue(it) })
    }

    fun fetchAvailableMenu(shopId: Long) {
        mShopPk = shopId
        mMenuData.addSource(mRepository.getAvailableMenu(shopId), { mMenuData.setValue(it) })
        resetMenuGroups()

        val resourceLiveData = Transformations.map(mMenuData) { menuModelResource ->
            mOriginalMenuGroups.setValue(Resource.loading(null))
            if (menuModelResource?.data == null)
                return@map null
            val groups = menuModelResource.data.groups
            groups.sortWith(Comparator { o1, o2 -> o1.category.compareTo(o2.category) })
            Resource.cloneResource(menuModelResource, groups)
        }
        mOriginalMenuGroups.addSource(resourceLiveData) { listResource ->
            mOriginalMenuGroups.value = listResource
            resetMenuGroups()
        }
    }

    fun resetMenuGroups() {
        mSelectedCategory.value = "default"
        mMenuGroups.value = mOriginalMenuGroups.value
    }

    fun clearFilters() {
        mFilteredString.value = null
        resetMenuItems()
        resetMenuGroups()
    }

    fun resetMenuItems() {
        mMenuItems.value = Resource.noRequest()
    }

    fun searchMenuItems(query: String?) {
        if (query.isNullOrEmpty())
            return
        mMenuItems.setValue(Resource.loading(null))
        if (mRunnable != null)
            mHandler.removeCallbacks(mRunnable)
        mRunnable = Runnable {
            val resourceLiveData: LiveData<Resource<List<MenuItemModel>>> = Transformations.map(mMenuData) { input ->
                if (input?.data == null)
                    return@map null
                val items = ArrayList<MenuItemModel>()
                for (groupModel in input.data.groups) {
                    for (itemModel in groupModel.items) {
                        if (itemModel.name.toLowerCase().contains(query.toLowerCase()))
                            items.add(itemModel)
                    }
                }
                if (items.size == 0)
                    return@map Resource.errorNotFound<List<MenuItemModel>>("Dish not found.")
                Resource.cloneResource<List<MenuItemModel>, MenuModel>(input, items)
            }
            mMenuItems.addSource(resourceLiveData) {
                mMenuItems.removeSource(resourceLiveData)
                mMenuItems.setValue(it)
            }
        }
        mHandler.postDelayed(mRunnable, 500)
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
        item.addCustomizationField(customizationField)
        mCurrentItem.value = item
    }

    fun setCurrentItem(item: OrderedItemModel) {
        try {
            mCurrentItem.value = item.clone()
        } catch (e: CloneNotSupportedException) {
        }

    }

    fun removeItemCustomization(customizationField: ItemCustomizationFieldModel) {
        val item = mCurrentItem.value ?: return
        item.removeCustomizationField(customizationField)
        mCurrentItem.value = item
    }

    fun setSelectedType(selectedType: Int) {
        val item = mCurrentItem.value ?: return
        item.typeIndex = selectedType
        mCurrentItem.value = item
    }

    fun changeQuantity(diff: Int) {
        val item = mCurrentItem.value ?: return
        if (diff != 0)
            setQuantity(item.quantity + diff)
    }

    fun cancelItem() = mCurrentItem.value?.let {
        it.quantity = 0
        it.changeCount = 0
        mCurrentItem.value = it
        resetItem()
    }

    fun canOrder(): Boolean = mCurrentItem.value?.canOrder() ?: false

    fun setQuantity(quantity: Int) {
        val item = mCurrentItem.value ?: return
        if (quantity != item.quantity) {
            item.quantity = quantity
            mCurrentItem.value = item
        }
        if (quantity == 0) {
            removeItem(item)
        }
    }

    fun orderItem() {
        updateCart()
        resetItem()
    }

    fun resetItem() {
        mCurrentItem.value = null
    }

    fun newOrderedItem(item: MenuItemModel) {
        val orderedItem = item.order(1)
        mCurrentItem.value = orderedItem
    }

    fun updateOrderedItem(item: MenuItemModel, count: Int): Boolean {
        val items = mOrderedItems.value
        var result = false
        if (items == null) {
            return result
        }
        var orderedItem: OrderedItemModel? = null
        var cartCount = 0
        for (listItem in items) {
            if (item == listItem.itemModel) {
                cartCount += listItem.quantity
                if (!item.isComplexItem) {
                    try {
                        orderedItem = listItem.clone()
                    } catch (e: CloneNotSupportedException) {
                    }

                    break
                }
            }
        }
        if (cartCount == 0) {
            return result
        }
        if (item.isComplexItem && cartCount != count) {
            orderedItem = item.order(1)
            setCurrentItem(orderedItem)
            result = true
        } else if (orderedItem != null && orderedItem.quantity != count) {
            orderedItem.quantity = count
            setCurrentItem(orderedItem)
            result = true
        }
        return result
    }

    private fun updateCart() {
        val item = mCurrentItem.value ?: return
        val orderedItems: MutableList<OrderedItemModel> = mOrderedItems.value?.toMutableList()
                ?: mutableListOf()
        val index = orderedItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 0) {
                item.quantity = orderedItems[index].quantity + item.changeCount
                orderedItems[index] = item
            } else
                orderedItems.removeAt(index)
        } else {
            if (item.itemModel.isComplexItem)
                item.quantity = item.changeCount
            orderedItems.add(item)
        }
        mOrderedItems.value = orderedItems
    }

    fun getOrderedCount(item: MenuItemModel): Int {
        var count = 0
        val orderedItems = orderedItems.value
        if (orderedItems != null) {
            for (orderedItem in orderedItems) {
                if (orderedItem.itemModel == item) {
                    count += orderedItem.quantity
                    if (!item.isComplexItem)
                        break
                }
            }
        }
        return count
    }

    fun removeItem(item: OrderedItemModel) {
        item.quantity = 0
        mCurrentItem.value = item
        mOrderedItems.value?.let {
            mOrderedItems.value = it.toMutableList().apply { remove(item) }
        }
        mCurrentItem.value = null
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

    fun confirmOrder() {
//        if (mSessionPk == null)
//            mResultOrder.addSource(mRepository.postActiveSessionOrders(mOrderedItems.value!!)) { mResultOrder.setValue(it) }
//        else
//            mResultOrder.addSource(mRepository.postManageSessionOrders(mSessionPk!!, mOrderedItems.value!!)) { mResultOrder.setValue(it) }
    }

    fun manageSession(sessionPk: Long) {
        mSessionPk = sessionPk
    }

    fun fetchRecommendedItems(shopId: Long) {
        mRecommendedData.addSource(mRepository.getRecommendedDishes(shopId)) { mRecommendedData.setValue(it) }
    }

    fun fetchTrendingItem() {
        mTrendingData.addSource(mActiveSessionRepository.getTrendingDishes(mShopPk)) { mTrendingData.setValue(it) }
    }

    suspend fun getMenuItemById(id: Long): MenuItemModel? = withContext(Dispatchers.Default) {
        mMenuData.value?.data?.let { menu ->
            for (group in menu.groups) {
                group.items.find { it.pk == id }?.also {
                    return@let it
                }
            }
            null
        }
    }

    fun getSelectedCategory() = mSelectedCategory

    fun getOriginalMenuGroups() = mOriginalMenuGroups

    fun filterMenuCategories(category: String) {
        mSelectedCategory.value?.let {
            if (it.equals(category, ignoreCase = true)) {
                mSelectedCategory.value = "default"
                resetMenuGroups()
                return
            }
            mSelectedCategory.value = category
        }

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
            Resource.cloneResource<List<MenuGroupModel>, List<MenuGroupModel>>(listResource, result)
        }
        mMenuGroups.addSource(resourceLiveData) {
            mMenuGroups.removeSource(resourceLiveData)
            mMenuGroups.value = it
        }
    }
}
