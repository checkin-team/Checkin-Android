package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.home.model.LiveSessionDetailModel
import com.checkin.app.checkin.home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.CartStatusModel
import com.checkin.app.checkin.session.SessionRepository
import com.checkin.app.checkin.session.models.ScheduledSessionStatus

class LiveSessionViewModel(application: Application) : BaseViewModel(application) {
    private val mSessionRepository = SessionRepository.getInstance(application)
    private val menuRepository = MenuRepository.getInstance(application)

    private val mScheduledSessionData = createNetworkLiveData<List<ScheduledLiveSessionDetailModel>>()
    private val mActiveSessionData = createNetworkLiveData<ActiveLiveSessionDetailModel>()
    private val mCartStatusData = createNetworkLiveData<CartStatusModel>()

    private val mLiveSessionData = MediatorLiveData<Resource<List<LiveSessionDetailModel>>>().apply {
        addSource(mScheduledSessionData) {
            it?.let { listResource ->
                if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                    value = value?.data?.filterIsInstance<ActiveLiveSessionDetailModel>()?.run { Resource.success(this + listResource.data) }
                            ?: listResource
                }
            }
        }
        addSource(mActiveSessionData) {
            it?.let { sessionResource ->
                if (sessionResource.status == Resource.Status.SUCCESS && sessionResource.data != null) {
                    value = value?.data?.filterIsInstance<ScheduledLiveSessionDetailModel>()?.run { Resource.success(listOf(sessionResource.data) + this) }
                            ?: Resource.cloneResource(sessionResource, listOf(sessionResource.data))
                }
            }
        }
    }

    val cartStatus: LiveData<Resource<CartStatusModel>> = mCartStatusData
    val liveSessionData: LiveData<Resource<List<LiveSessionDetailModel>>> = mLiveSessionData

    override fun updateResults() {
        fetchScheduledSessions()
        fetchLiveActiveSession()
        fetchCartStatus()
    }

    fun fetchCartStatus() {
        mCartStatusData.addSource(menuRepository.checkCartStatus, mCartStatusData::setValue)
    }

    fun fetchScheduledSessions() {
        mScheduledSessionData.addSource(mSessionRepository.getScheduledSessions()) {
            mScheduledSessionData.value = it?.data?.let { data ->
                Resource.cloneResource(it, data.filter { it.scheduled.status != ScheduledSessionStatus.DONE })
            } ?: it
        }
    }

    fun fetchLiveActiveSession() {
        mActiveSessionData.addSource(mSessionRepository.activeSessionLiveStatus, mActiveSessionData::setValue)
    }
}
