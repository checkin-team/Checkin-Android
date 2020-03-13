package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.HomeRepository
import com.checkin.app.checkin.home.model.ClosedSessionDetailsModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel

class ClosedSessionViewModel(application: Application) : BaseViewModel(application) {

    private val homeRepository = HomeRepository.getInstance(application)

    private val mSessionData = createNetworkLiveData<ClosedSessionDetailsModel>()

    val ordersData: LiveData<Resource<List<SessionOrderedItemModel>>> = Transformations.map(mSessionData) {
        it?.data?.let { data ->
            Resource.cloneResource(it, data.orderedItems)
        } ?: Resource.cloneResource(it, emptyList<SessionOrderedItemModel>())
    }

    val sessionData: LiveData<Resource<ClosedSessionDetailsModel>> = mSessionData

    private val PaymentDetailsViewModel = HomeRepository.getInstance(application)

    override fun updateResults() {

    }

    fun fetchSessionData(sessionId: Long) {
        mSessionData.addSource(homeRepository.getCustomerClosedSessionDetails(sessionId), mSessionData::setValue)
    }

}
