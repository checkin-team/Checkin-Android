package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.session.activesession.ActiveSessionRepository
import com.checkin.app.checkin.session.models.TrendingDishModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserMenuViewModel(application: Application) : BaseMenuViewModel(application) {
    private val mActiveSessionRepository: ActiveSessionRepository = ActiveSessionRepository.getInstance(application)

    private val mTrendingData = createNetworkLiveData<List<TrendingDishModel>>()
    private val mRecommendedData = createNetworkLiveData<List<TrendingDishModel>>()

    val recommendedItems: LiveData<Resource<List<TrendingDishModel>>> = mRecommendedData

    val menuTrendingItems: LiveData<Resource<List<TrendingDishModel>>> = mTrendingData

    fun fetchRecommendedItems() {
        mRecommendedData.addSource(mRepository.getRecommendedDishes(mShopPk), mRecommendedData::setValue)
    }

    fun fetchTrendingItem() {
        mTrendingData.addSource(mActiveSessionRepository.getTrendingDishes(mShopPk), mTrendingData::setValue)
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
}
