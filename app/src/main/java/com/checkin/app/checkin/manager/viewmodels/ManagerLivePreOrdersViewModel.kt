package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.manager.models.PreparationTimeModel
import com.checkin.app.checkin.manager.models.ShopScheduledSessionDetailModel
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel

class ManagerLivePreOrdersViewModel(application: Application) : BaseViewModel(application) {
    private val managerRepository = ManagerRepository.getInstance(application)

    private val mPreOrdersData = createNetworkLiveData<List<ShopScheduledSessionModel>>()
    private val mSessionData = createNetworkLiveData<ShopScheduledSessionDetailModel>()

    var shopPk: Long = 0
        private set
    var sessionPk: Long = 0
        private set

    val preparationTimeData = MutableLiveData<PreparationTimeModel>(PreparationTimeModel(10))
    val preOrdersData: LiveData<Resource<List<ShopScheduledSessionModel>>> = mPreOrdersData
    val sessionData: LiveData<Resource<ShopScheduledSessionDetailModel>> = mSessionData

    fun fetchPreOrders(shopPk: Long) {
        this.shopPk = shopPk
        mPreOrdersData.addSource(managerRepository.getRestaurantScheduledSession(shopPk), mPreOrdersData::setValue)
    }

    fun fetchSessionData(sessionId: Long) {
        this.sessionPk = sessionId
        mSessionData.addSource(managerRepository.getScheduledSessionDetail(sessionId), mSessionData::setValue)
    }

    override fun updateResults() {
        if (shopPk != 0L) fetchPreOrders(shopPk)
        if (sessionPk != 0L) fetchSessionData(sessionPk)
    }
}