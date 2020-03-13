package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.model.ClosedSessionBriefModel
import com.checkin.app.checkin.home.model.ClosedSessionDetailsModel
import com.checkin.app.checkin.session.SessionRepository
import com.checkin.app.checkin.session.models.SessionOrderedItemModel

class ClosedSessionViewModel(application: Application) : BaseViewModel(application) {

    private var sessionId: Long = 0

    private val sessionRepository = SessionRepository.getInstance(application)

    private val mSessionData = createNetworkLiveData<ClosedSessionDetailsModel>()

    private val mCustomerClosedSessionlist = createNetworkLiveData<List<ClosedSessionBriefModel>>()

    val customerClosedList: LiveData<Resource<List<ClosedSessionBriefModel>>> = mCustomerClosedSessionlist

    fun fetchCustomerTransaction() {
        mCustomerClosedSessionlist.addSource(sessionRepository.customerClosedSessionList, mCustomerClosedSessionlist::setValue)
    }

    fun fetchSessionData(sessionId: Long) {
        this.sessionId = sessionId
        mSessionData.addSource(sessionRepository.getCustomerClosedSessionDetails(sessionId), mSessionData::setValue)
    }

    val ordersData: LiveData<Resource<List<SessionOrderedItemModel>>> = Transformations.map(mSessionData) {
        it?.data?.let { data ->
            Resource.cloneResource(it, data.orderedItems)
        } ?: Resource.cloneResource(it, emptyList<SessionOrderedItemModel>())
    }

    val sessionData: LiveData<Resource<ClosedSessionDetailsModel>> = mSessionData

    override fun updateResults() {
        if (sessionId != 0L && mSessionData.value?.status != Resource.Status.LOADING) {
            fetchSessionData(sessionId)
        } else {
            fetchCustomerTransaction()
        }
    }
}
