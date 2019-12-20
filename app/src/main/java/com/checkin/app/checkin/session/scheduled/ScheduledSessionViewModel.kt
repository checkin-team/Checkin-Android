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
    val sessionRepository = SessionRepository.getInstance(application)

    val mNewSessionData = createNetworkLiveData<NewScheduledSessionModel>()
    val mClearCart = createNetworkLiveData<ObjectNode>()
    val mQrResult = createNetworkLiveData<QRResultModel>()

    val newScheduledSessionData: LiveData<Resource<NewScheduledSessionModel>> = mNewSessionData
    val newQrSessionData: LiveData<Resource<QRResultModel>> = mQrResult
    val clearCartData: LiveData<Resource<ObjectNode>> = mClearCart

    private fun createNewScheduledSession(countPeople: Int, plannedTime: Date, remarks: String?) {
        val body = NewScheduledSessionModel(countPeople = countPeople, plannedDatetime = plannedTime, remarks = remarks)
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

    fun syncScheduleInfo(selectedDate: Date, countPeople: Int) {
        if (sessionPk == 0L) createNewScheduledSession(countPeople, selectedDate, null)
        else updateScheduledSessionInfo(countPeople, selectedDate)
    }

    override fun updateResults() {
    }

    fun clearCart() {
        mClearCart.addSource(sessionRepository.clearCustomerCart, mClearCart::setValue)
    }
}