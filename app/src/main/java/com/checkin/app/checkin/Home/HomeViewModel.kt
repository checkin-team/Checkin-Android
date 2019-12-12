package com.checkin.app.checkin.Home

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Converters
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel
import com.checkin.app.checkin.Shop.ShopRepository
import com.checkin.app.checkin.session.SessionRepository
import com.checkin.app.checkin.session.model.QRResultModel
import com.checkin.app.checkin.session.model.SessionBasicModel
import com.fasterxml.jackson.databind.node.ObjectNode

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val mSessionRepository: SessionRepository = SessionRepository.getInstance(application)
    private val mShopRepository: ShopRepository = ShopRepository.getInstance(application)

    private val mQrResult = createNetworkLiveData<QRResultModel>()
    private val mSessionStatus = createNetworkLiveData<SessionBasicModel>()
    private val mCancelDineInRequest = createNetworkLiveData<ObjectNode>()
    private val mNearbyRestaurants = createNetworkLiveData<List<NearbyRestaurantModel>>()

    val sessionStatus: LiveData<Resource<SessionBasicModel>>
        get() = mSessionStatus

    val qrResult: LiveData<Resource<QRResultModel>>
        get() = mQrResult

    val cancelDineInData: LiveData<Resource<ObjectNode>>
        get() = mCancelDineInRequest

    val nearbyRestaurants: LiveData<Resource<List<NearbyRestaurantModel>>>
        get() = mNearbyRestaurants

    override fun updateResults() {
        fetchSessionStatus()
    }

    fun processQr(data: String) {
        val requestJson = Converters.objectMapper.createObjectNode()
        requestJson.put("data", data)
        mQrResult.addSource(mSessionRepository.newCustomerSession(requestJson)) { mQrResult.setValue(it) }
    }

    fun fetchSessionStatus() {
        mSessionStatus.addSource(mSessionRepository.activeSessionCheck) { mSessionStatus.setValue(it) }
    }

    fun cancelUserWaitingDineIn() {
        mCancelDineInRequest.addSource(mSessionRepository.cancelSessionJoinRequest()) { mCancelDineInRequest.setValue(it) }
    }

    fun fetchNearbyRestaurants() {
        mNearbyRestaurants.addSource(mShopRepository.nearbyRestaurants) { mNearbyRestaurants.setValue(it) }
    }
}
