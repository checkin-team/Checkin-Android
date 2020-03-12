package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.HomeRepository
import com.checkin.app.checkin.home.model.PastSessionDetailsModel
import com.checkin.app.checkin.home.model.SessionOrderItemModel

class PaymentDetailsViewModel(application: Application) : BaseViewModel(application) {

    private val homeRepository = HomeRepository.getInstance(application)

    private val mSessionData = createNetworkLiveData<PastSessionDetailsModel>()

    val ordersData: LiveData<Resource<List<SessionOrderItemModel>>> = Transformations.map(mSessionData) {
        it?.data?.let { data ->
            Resource.cloneResource(it, data.orderedItems)
        } ?: Resource.cloneResource(it, emptyList<SessionOrderItemModel>())
    }

    val sessionData = mSessionData

    private val PaymentDetailsViewModel = HomeRepository.getInstance(application)
    override fun updateResults() {

    }

    fun fetchSessionData(sessionId: Long) {
        mSessionData.addSource(homeRepository.getCustomerPastSessionDetails(sessionId), mSessionData::setValue)
    }

}
