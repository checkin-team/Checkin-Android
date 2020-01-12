package com.checkin.app.checkin.session.scheduled.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.session.scheduled.ScheduledSessionRepository
import com.fasterxml.jackson.databind.node.ObjectNode

class ScheduledSessionDetailViewModel(application: Application) : BaseViewModel(application) {
    private val sessionRepository = ScheduledSessionRepository.getInstance(application)

    private val mSessionData = createNetworkLiveData<CustomerScheduledSessionDetailModel>()
    private val mCancelData = createNetworkLiveData<ObjectNode>()

    var sessionId: Long = 0
        private set

    val cancelData: LiveData<Resource<ObjectNode>> = mCancelData
    val sessionData: LiveData<Resource<CustomerScheduledSessionDetailModel>> = mSessionData
    val ordersData: LiveData<Resource<List<SessionOrderedItemModel>>> = Transformations.map(mSessionData) {
        it?.data?.let { data ->
            Resource.cloneResource(it, data.orderedItems)
        } ?: Resource.cloneResource(it, emptyList<SessionOrderedItemModel>())
    }


    fun fetchSessionData(sessionId: Long) {
        this.sessionId = sessionId
        mSessionData.addSource(sessionRepository.getCustomerScheduledSessionDetail(sessionId), mSessionData::setValue)
    }

    fun cancelSession() {
        mCancelData.addSource(sessionRepository.deleteCustomerScheduledSession(sessionId), mCancelData::setValue)
    }

    override fun updateResults() {
    }

    fun updateStatus(status: ScheduledSessionStatus) {
        mSessionData.value?.data?.run {
            mSessionData.value = Resource.cloneResource(mSessionData.value, copy(scheduled = scheduled.copy().apply { this.status = status }))
        }
    }
}