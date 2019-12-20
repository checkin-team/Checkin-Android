package com.checkin.app.checkin.session.scheduled

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Converters
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.session.SessionRepository
import com.checkin.app.checkin.session.models.NewScheduledSessionModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.*

class ScheduledSessionViewModel(application: Application) : BaseViewModel(application) {
    var sessionPk: Long = 0
    private val sessionRepository = SessionRepository.getInstance(application)

    private val mNewSessionData = createNetworkLiveData<NewScheduledSessionModel>()
    private val mClearCart = createNetworkLiveData<ObjectNode>()
    private val mQrResult = createNetworkLiveData<QRResultModel>()

    val newScheduledSessionData: LiveData<Resource<NewScheduledSessionModel>> = mNewSessionData
    val newQrSessionData: LiveData<Resource<QRResultModel>> = mQrResult
    val clearCartData: LiveData<Resource<ObjectNode>> = mClearCart

    private fun createNewScheduledSession(countPeople: Int, plannedTime: Date, restaurantId: Long, remarks: String?) {
        val body = NewScheduledSessionModel(countPeople = countPeople, plannedDatetime = plannedTime, remarks = remarks).apply {
            this.restaurantId = restaurantId
        }
        mNewSessionData.addSource(sessionRepository.newScheduledSession(body), mNewSessionData::setValue)
    }

    private fun updateScheduledSessionInfo(countPeople: Int, plannedTime: Date) {
        val body = NewScheduledSessionModel(countPeople = countPeople, plannedDatetime = plannedTime)
        mNewSessionData.addSource(sessionRepository.editScheduledSession(sessionPk, body), mNewSessionData::setValue)
    }

    fun updateScheduledSessionRemarks(remarks: String?) {
        val body = NewScheduledSessionModel(remarks = remarks)
        mNewSessionData.addSource(sessionRepository.editScheduledSession(sessionPk, body), mNewSessionData::setValue)
    }

    fun createNewQrSession(data: String) {
        val requestJson = Converters.objectMapper.createObjectNode()
        requestJson.put("data", data)
        mQrResult.addSource(sessionRepository.newCustomerSession(requestJson), mQrResult::setValue)
    }

    fun syncScheduleInfo(selectedDate: Date, countPeople: Int, restaurantId: Long) {
        if (sessionPk == 0L) createNewScheduledSession(countPeople, selectedDate, restaurantId, null)
        else updateScheduledSessionInfo(countPeople, selectedDate)
    }

    override fun updateResults() {
    }

    fun clearCart() {
        mClearCart.addSource(sessionRepository.clearCustomerCart, mClearCart::setValue)
    }
}