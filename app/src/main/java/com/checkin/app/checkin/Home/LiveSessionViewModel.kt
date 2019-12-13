package com.checkin.app.checkin.Home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.Home.model.LiveSessionDetail
import com.checkin.app.checkin.Home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.session.SessionRepository

class LiveSessionViewModel(application: Application) : BaseViewModel(application) {
    private val mSessionRepository = SessionRepository.getInstance(application)

    private val mScheduledSessionData = createNetworkLiveData<List<ScheduledLiveSessionDetailModel>>()
    private val mActiveSessionData = createNetworkLiveData<ActiveLiveSessionDetailModel>()

    private val mLiveSessionData = MediatorLiveData<Resource<List<LiveSessionDetail>>>().apply {
        addSource(mScheduledSessionData) {
            it?.let { listResource ->
                value = if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                    value?.data?.filterIsInstance<ActiveLiveSessionDetailModel>()?.run { Resource.success(this + listResource.data) }
                            ?: listResource
                } else listResource
            }
        }
        addSource(mActiveSessionData) {
            it?.let { sessionResource ->
                value = if (sessionResource.status == Resource.Status.SUCCESS && sessionResource.data != null) {
                    value?.data?.filterIsInstance<ScheduledLiveSessionDetailModel>()?.run { Resource.success(listOf(sessionResource.data) + this) }
                            ?: Resource.cloneResource(sessionResource, listOf(sessionResource.data))
                } else Resource.cloneResource(sessionResource, emptyList())
            }
        }
    }

    val liveSessionData: LiveData<Resource<List<LiveSessionDetail>>>
        get() = mLiveSessionData

    override fun updateResults() {
        fetchScheduledSessions()
    }

    fun fetchScheduledSessions() {
        mScheduledSessionData.addSource(mSessionRepository.getScheduledSessions(), mScheduledSessionData::setValue)
    }

    fun getSessionDetailByPk(pk: Long) = Transformations.map(mLiveSessionData) {
        it?.let { liveResource ->
            if (liveResource.status == Resource.Status.SUCCESS && liveResource.data != null)
                liveResource.data.find { sessionData ->
                    when (sessionData) {
                        is ScheduledLiveSessionDetailModel -> sessionData.pk == pk
                        is ActiveLiveSessionDetailModel -> sessionData.pk == pk
                    }
                }
        }
    }

}
