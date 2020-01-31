package com.checkin.app.checkin.session.scheduled.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.network.RetrofitCallAsyncTask
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.data.resource.Resource.Companion.cloneResource
import com.checkin.app.checkin.data.resource.Resource.Companion.error
import com.checkin.app.checkin.data.resource.Resource.Companion.errorNotFound
import com.checkin.app.checkin.data.resource.Resource.Companion.loading
import com.checkin.app.checkin.data.resource.Resource.Companion.success
import com.checkin.app.checkin.misc.paytm.PaytmModel
import com.checkin.app.checkin.session.SessionRepository
import com.checkin.app.checkin.session.models.NewScheduledSessionModel
import com.checkin.app.checkin.session.models.PromoDetailModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.checkin.app.checkin.session.models.SessionPromoModel
import com.checkin.app.checkin.session.scheduled.ScheduledSessionRepository
import com.checkin.app.checkin.utility.ProgressRequestBody.UploadCallbacks
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class NewScheduledSessionViewModel(application: Application) : BaseViewModel(application) {
    private val sessionRepository = SessionRepository.getInstance(application)
    private val scheduledSessionRepository = ScheduledSessionRepository.getInstance(application)

    private val mNewSessionData = createNetworkLiveData<NewScheduledSessionModel>()
    private val mClearCart = createNetworkLiveData<ObjectNode>()
    private val mQrResult = createNetworkLiveData<QRResultModel>()
    private val mPromoRemove = createNetworkLiveData<ObjectNode>()
    private val mPaytmCallbackData = createNetworkLiveData<ObjectNode>()
    private val mPaytmData = createNetworkLiveData<PaytmModel>()
    private val mPromoData = createNetworkLiveData<List<PromoDetailModel>>()
    private val mSessionPromo = createNetworkLiveData<SessionPromoModel>()

    private var sessionRemarksJob: Job? = null

    lateinit var retrySessionBody: NewScheduledSessionModel
    lateinit var retryQrData: String

    var sessionPk: Long = 0
    var isPhoneVerified = true

    val newScheduledSessionData: LiveData<Resource<NewScheduledSessionModel>> = mNewSessionData
    val newQrSessionData: LiveData<Resource<QRResultModel>> = mQrResult
    val clearCartData: LiveData<Resource<ObjectNode>> = mClearCart
    val promoCodes: LiveData<Resource<List<PromoDetailModel>>> = mPromoData
    val paytmData: LiveData<Resource<PaytmModel>> = mPaytmData
    val paytmCallbackData: LiveData<Resource<ObjectNode>> = mPaytmCallbackData

    val promoDeletedData: LiveData<Resource<ObjectNode>> = Transformations.map(mPromoRemove) { input ->
        if (input != null && input.status === Resource.Status.SUCCESS) {
            mSessionPromo.value = errorNotFound(null)
        }
        input
    }

    private var lastPaymentBundle: Bundle? = null
    val sessionAppliedPromo: LiveData<Resource<SessionPromoModel>> = mSessionPromo

    private fun createNewScheduledSession(countPeople: Int, plannedTime: Date, restaurantId: Long, remarks: String?) {
        val body = NewScheduledSessionModel(countPeople = countPeople, plannedDatetime = plannedTime, remarks = remarks).apply {
            this.restaurantId = restaurantId
        }
        retrySessionBody = body
        mNewSessionData.addSource(sessionRepository.newScheduledSession(body), mNewSessionData::setValue)
    }

    private fun createNewScheduledSession(body: NewScheduledSessionModel) {
        retrySessionBody = body
        mNewSessionData.addSource(sessionRepository.newScheduledSession(body), mNewSessionData::setValue)
    }

    private fun updateScheduledSessionInfo(countPeople: Int, plannedTime: Date) {
        val body = NewScheduledSessionModel(countPeople = countPeople, plannedDatetime = plannedTime)
        mNewSessionData.addSource(scheduledSessionRepository.editScheduledSession(sessionPk, body), mNewSessionData::setValue)
    }

    fun updateScheduledSessionRemarks(remarks: String?) {
        sessionRemarksJob?.cancel("Overridden by new job")

        sessionRemarksJob = viewModelScope.launch {
            delay(500)
            val body = NewScheduledSessionModel(remarks = remarks)
            mNewSessionData.addSource(scheduledSessionRepository.editScheduledSession(sessionPk, body), mNewSessionData::setValue)
        }
    }

    fun postPaytmCallback(bundle: Bundle) {
        lastPaymentBundle = bundle
        val data = objectMapper.createObjectNode()
        val keys = bundle.keySet()
        for (key in keys) {
            data.put(key, bundle[key]?.toString())
        }
        val listener: UploadCallbacks = object : UploadCallbacks {
            override fun onProgressUpdate(percentage: Int) {
                mPaytmCallbackData.postValue(loading<ObjectNode>(null))
            }

            override fun onSuccess() {
                mPaytmCallbackData.postValue(success<ObjectNode>(null))
            }

            override fun onFailure() {
                mPaytmCallbackData.postValue(error<ObjectNode>("Sorry, but PayTM transaction failed", null))
            }
        }
        doPostPaytmCallback(data, listener)
    }

    fun retrySessionCreation() {
        if (::retryQrData.isInitialized) {
            createNewQrSession(retryQrData)
        }
        if (::retrySessionBody.isInitialized) {
            createNewScheduledSession(retrySessionBody)
        }
    }

    fun retryPostPaytmCallback() = lastPaymentBundle?.let { postPaytmCallback(it) }

    fun requestPaytmDetails() {
        mPaytmData.addSource(scheduledSessionRepository.postPaytmDetailRequest(sessionPk), mPaytmData::setValue)
    }

    private fun doPostPaytmCallback(data: ObjectNode, listener: UploadCallbacks) {
        RetrofitCallAsyncTask<ObjectNode>(listener).execute(scheduledSessionRepository.syncPostPaytmCallback(data))
    }

    fun createNewQrSession(data: String) {
        val requestJson = objectMapper.createObjectNode()
        retryQrData = data
        requestJson.put("data", data)
        mQrResult.addSource(sessionRepository.newCustomerSession(requestJson), mQrResult::setValue)
    }

    fun syncScheduleInfo(selectedDate: Date, countPeople: Int, restaurantId: Long) {
        if (sessionPk == 0L) createNewScheduledSession(countPeople, selectedDate, restaurantId, null)
        else updateScheduledSessionInfo(countPeople, selectedDate)
    }

    fun removePromoCode() {
        mPromoRemove.addSource(scheduledSessionRepository.removePromoCode(sessionPk), mPromoRemove::setValue)
    }

    fun availPromoCode(code: String?) {
        val data = objectMapper.createObjectNode()
                .put("code", code)
        mSessionPromo.addSource(scheduledSessionRepository.postAvailPromoCode(sessionPk, data)) {
            it?.let { sessionPromoModelResource ->
                mData.value = cloneResource(sessionPromoModelResource, data)
                if (sessionPromoModelResource.status === Resource.Status.SUCCESS && sessionPromoModelResource.data != null) mSessionPromo.value = sessionPromoModelResource
            }
        }
    }

    fun fetchPromoCodes(restaurantId: Long) {
        val liveData = if (restaurantId > 0) {
            sessionRepository.getRestaurantPromoCodes(restaurantId, false)
        } else sessionRepository.allPromoCodes
        mPromoData.addSource(liveData, mPromoData::setValue)
    }

    val isOfferApplied: Boolean = mSessionPromo.value?.data != null

    fun fetchSessionAppliedPromo() {
        mSessionPromo.addSource(scheduledSessionRepository.getSessionAppliedPromo(sessionPk), mSessionPromo::setValue)
    }

    override fun updateResults() {
    }

    fun clearCart() {
        mClearCart.addSource(sessionRepository.clearCustomerCart, mClearCart::setValue)
    }
}