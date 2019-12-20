package com.checkin.app.checkin.menu.viewmodels

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.Comparator

class UserMenuViewModel(application: Application) : BaseViewModel(application) {
    private val mHandler = Handler()
    private val mRepository: MenuRepository = MenuRepository.getInstance(application)
    private val mActiveSessionRepository: ActiveSessionRepository = ActiveSessionRepository.getInstance(application)

    private val mMenuData = createNetworkLiveData<MenuModel>()
    private var mOriginalMenuGroups = createNetworkLiveData<List<MenuGroupModel>>()
    private val mMenuGroups = MediatorLiveData<Resource<List<MenuGroupModel>>>()
    private val mMenuItems = createNetworkLiveData<List<MenuItemModel>>()
    private val mTrendingData = createNetworkLiveData<List<TrendingDishModel>>()
    private val mRecommendedData = createNetworkLiveData<List<TrendingDishModel>>()

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

    val filteredString: LiveData<String>
        get() = mFilteredString

    val recommendedItems: LiveData<Resource<List<TrendingDishModel>>>
        get() = mRecommendedData

    val menuTrendingItems: LiveData<Resource<List<TrendingDishModel>>>
        get() = mTrendingData

    override fun updateResults() {
        mMenuData.addSource(mRepository.getAvailableMenu(mShopPk), mMenuData::setValue)
    }

    fun fetchAvailableMenu(shopId: Long) {
        mShopPk = shopId
        mMenuData.addSource(mRepository.getAvailableMenu(shopId), mMenuData::setValue)
        resetMenuGroups()

        val resourceLiveData = Transformations.map(mMenuData) { menuModelResource ->
            mOriginalMenuGroups.value = Resource.loading(null)
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

    fun cancelItem() = mCurrentItem.value?.let {
        it.quantity = 0
        it.changeCount = 0
        mCurrentItem.value = it
        resetItem()
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
