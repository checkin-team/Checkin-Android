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
    private val sessionRepository = SessionRepository.getInstance(application)

    private var sessionId: Long = 0

    private val mSessionData = createNetworkLiveData<ClosedSessionDetailsModel>()
    private val mClosedSessions = createNetworkLiveData<List<ClosedSessionBriefModel>>()

    val customerClosedList: LiveData<Resource<List<ClosedSessionBriefModel>>> = mClosedSessions
    val sessionData: LiveData<Resource<ClosedSessionDetailsModel>> = mSessionData
    val ordersData: LiveData<Resource<List<SessionOrderedItemModel>>> = Transformations.map(mSessionData) {
        it?.data?.let { data ->
            Resource.cloneResource(it, data.orderedItems)
        } ?: Resource.cloneResource(it, emptyList<SessionOrderedItemModel>())
    }

    fun fetchClosedSessions() {
        mClosedSessions.addSource(sessionRepository.customerClosedSessionList, mClosedSessions::setValue)
    }

    fun fetchSessionData(sessionId: Long) {
        this.sessionId = sessionId
        mSessionData.addSource(sessionRepository.getCustomerClosedSessionDetails(sessionId), mSessionData::setValue)
    }

    override fun fetchMissing() {
        super.fetchMissing()
        if (sessionId != 0L && mSessionData.value?.mayLoad == false) fetchSessionData(sessionId)
        else if (mClosedSessions.value?.mayLoad == false) fetchClosedSessions()
    }

    override fun updateResults() {
        if (sessionId != 0L && mSessionData.value?.mayLoad == false) fetchSessionData(sessionId)
        else fetchClosedSessions()
    }
}
