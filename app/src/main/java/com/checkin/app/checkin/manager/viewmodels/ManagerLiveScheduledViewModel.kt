package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.manager.models.*
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import java.util.*

class ManagerLiveScheduledViewModel(application: Application) : BaseViewModel(application) {
    private val managerRepository = ManagerRepository.getInstance(application)

    private val mScheduledOrders = createNetworkLiveData<List<ShopScheduledSessionModel>>()
    private val mSessionData = createNetworkLiveData<ShopScheduledSessionDetailModel>()
    private val mAcceptData = createNetworkLiveData<PreparationTimeModel>()
    private val mRejectData = createNetworkLiveData<GenericDetailModel>()
    private val mDoneData = createNetworkLiveData<ScheduledSessionDoneModel>()

    var shopPk: Long = 0
        private set
    var sessionPk: Long = 0
        private set

    val preparationTimeData by lazy { MutableLiveData<PreparationTimeModel>(PreparationTimeModel(10)) }
    val preOrdersData: LiveData<Resource<List<ShopScheduledSessionModel>>> by lazy {
        Transformations.map(mScheduledOrders) {
            it?.let {
                Resource.cloneResource(it, it.data.takeIf { it.isNotEmpty() }?.filter { it.isPreDining }
                        ?: emptyList())
            } ?: it
        }
    }
    val qsrOrdersData: LiveData<Resource<List<ShopScheduledSessionModel>>> by lazy {
        Transformations.map(mScheduledOrders) {
            it?.let {
                Resource.cloneResource(it, it.data.takeIf { it.isNotEmpty() }?.filter { !it.isPreDining }
                        ?: emptyList())
            } ?: it
        }
    }
    val sessionData: LiveData<Resource<ShopScheduledSessionDetailModel>> = mSessionData
    val acceptData: LiveData<Resource<PreparationTimeModel>> = Transformations.map(mAcceptData) { input ->
        if (input?.status == Resource.Status.SUCCESS && input.data != null) {
            mSessionData.value?.data?.let {
                mSessionData.value = Resource.cloneResource(mSessionData.value, it.copy(scheduled = it.scheduled.copy().apply { status = ScheduledSessionStatus.ACCEPTED }))
            }
            mScheduledOrders.value?.data?.let {
                val index = it.indexOfFirst { it.pk == input.data.pk }
                if (index != -1) mScheduledOrders.value = Resource.cloneResource(mScheduledOrders.value, it.toMutableList().apply {
                    this[index] = this[index].run { copy(scheduled = scheduled.copy().apply { status = ScheduledSessionStatus.ACCEPTED }) }
                })
            }
        }
        input
    }
    val rejectData: LiveData<Resource<GenericDetailModel>> = Transformations.map(mRejectData) { input ->
        if (input?.status == Resource.Status.SUCCESS && input.data != null) {
            mSessionData.value?.data?.let {
                mSessionData.value = Resource.cloneResource(mSessionData.value, it.copy(scheduled = it.scheduled.copy().apply { status = ScheduledSessionStatus.CANCELLED_BY_RESTAURANT }))
            }
            mScheduledOrders.value?.data?.let {
                val index = it.indexOfFirst { it.pk == input.data.longPk }
                if (index != -1) mScheduledOrders.value = Resource.cloneResource(mScheduledOrders.value, it.toMutableList().apply { removeAt(index) })
            }
        }
        input
    }
    val doneData: LiveData<Resource<ScheduledSessionDoneModel>> = Transformations.map(mDoneData) { input ->
        if (input?.status == Resource.Status.SUCCESS && input.data != null) {
            val currTime = Calendar.getInstance().time
            mSessionData.value?.data?.let {
                mSessionData.value = Resource.cloneResource(mSessionData.value, it.copy(scheduled = it.scheduled.copy(modified = currTime).apply { status = ScheduledSessionStatus.DONE }))
            }
            mScheduledOrders.value?.data?.let {
                val index = it.indexOfFirst { it.pk == input.data.pk }
                if (index != -1) {
                    mScheduledOrders.value = if (input.data.isCheckedOut) Resource.cloneResource(mScheduledOrders.value, it.toMutableList().apply { removeAt(index) })
                    else {
                        val data = it.toMutableList().apply {
                            this[index] = this[index].run { copy(scheduled = scheduled.copy(modified = currTime).apply { status = ScheduledSessionStatus.DONE }) }
                        }
                        Resource.cloneResource(mScheduledOrders.value, data)
                    }
                }
            }
        }
        input
    }

    fun fetchScheduledSessions(shopPk: Long) {
        this.shopPk = shopPk
        mScheduledOrders.addSource(managerRepository.getRestaurantScheduledSession(shopPk), mScheduledOrders::setValue)
    }

    fun fetchSessionData(sessionId: Long) {
        this.sessionPk = sessionId
        mSessionData.addSource(managerRepository.getScheduledSessionDetail(sessionId), mSessionData::setValue)
    }

    fun acceptSession(sessionId: Long) {
        mAcceptData.addSource(managerRepository.acceptScheduledSession(sessionId, preparationTimeData.value!!), mAcceptData::setValue)
    }

    fun rejectSession(sessionId: Long, reasonType: Int, reasonMessage: String?) {
        val data = ScheduledSessionCancelModel(reasonType, reasonMessage)
        mRejectData.addSource(managerRepository.rejectScheduledSession(sessionId, data), mRejectData::setValue)
    }

    fun markSessionDone(sessionId: Long) {
        mDoneData.addSource(managerRepository.doneScheduledSession(sessionId), mDoneData::setValue)
    }

    override fun updateResults() {
        if (shopPk != 0L) fetchScheduledSessions(shopPk)
        if (sessionPk != 0L) fetchSessionData(sessionPk)
    }

    fun removeSession(pk: Long) {
        mScheduledOrders.value?.data?.let {
            mScheduledOrders.value = Resource.cloneResource(mScheduledOrders.value, it.toMutableList().apply { removeAll { it.pk == pk } })
        }
    }

    fun updateSessionStatus(pk: Long, status: ScheduledSessionStatus) {
        mScheduledOrders.value?.data?.let {
            val index = it.indexOfFirst { it.pk == pk }
            mScheduledOrders.value = Resource.cloneResource(mScheduledOrders.value, it.toMutableList().apply {
                this[index] = this[index].let { it.copy(scheduled = it.scheduled.apply { this.status = status }) }
            })
        }
    }
}