package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel

class ManagerLivePreOrdersViewModel(application: Application) : BaseViewModel(application) {
    private val managerRepository = ManagerRepository.getInstance(application)

    private val mPreOrdersData = createNetworkLiveData<List<ShopScheduledSessionModel>>()

    var shopPk: Long = 0
        private set

    val preOrdersData: LiveData<Resource<List<ShopScheduledSessionModel>>> = mPreOrdersData

    fun fetchPreorders(shopPk: Long) {
        this.shopPk = shopPk
        mPreOrdersData.addSource(managerRepository.getRestaurantScheduledSession(shopPk), mPreOrdersData::setValue)
    }

    override fun updateResults() {
    }
}