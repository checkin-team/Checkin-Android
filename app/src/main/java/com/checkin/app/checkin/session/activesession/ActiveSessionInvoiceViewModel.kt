package com.checkin.app.checkin.session.activesession

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE
import com.checkin.app.checkin.Utility.ProgressRequestBody.UploadCallbacks
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
import com.checkin.app.checkin.session.models.CheckoutStatusModel
import com.checkin.app.checkin.session.models.PromoDetailModel
import com.checkin.app.checkin.session.models.SessionInvoiceModel
import com.checkin.app.checkin.session.models.SessionPromoModel
import com.fasterxml.jackson.databind.node.ObjectNode

class ActiveSessionInvoiceViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: ActiveSessionRepository = ActiveSessionRepository.getInstance(application)
    private val mSessionRepository: SessionRepository = SessionRepository.getInstance(application)

    private val mInvoiceData = createNetworkLiveData<SessionInvoiceModel>()
    private val mCheckoutData = createNetworkLiveData<CheckoutStatusModel>()
    private val mPaytmData = createNetworkLiveData<PaytmModel>()
    private val mPromoList = createNetworkLiveData<List<PromoDetailModel>>()
    private val mSessionPromo = createNetworkLiveData<SessionPromoModel>()
    private val mPromoDeletedData = createNetworkLiveData<ObjectNode>()
    private val mPaytmCallbackData = createNetworkLiveData<ObjectNode>()
    private val mSessionCancelCheckoutData = createNetworkLiveData<ObjectNode>()
    private val mIsRequestedCheckout = MutableLiveData(false)

    var isSessionBenefitsShown = false
        private set
    var isSessionPromoInvalid = false
    val sessionInvoice: LiveData<Resource<SessionInvoiceModel>> = mInvoiceData

    fun fetchSessionInvoice() {
        mInvoiceData.addSource(mRepository.activeSessionInvoice, mInvoiceData::setValue)
    }

    fun requestCheckout(tip: Double, paymentMode: PAYMENT_MODE, override: Boolean) {
        val data = objectMapper.createObjectNode()
                .put("tip", tip)
                .put("payment_mode", paymentMode.tag)
                .put("override", override)
        mCheckoutData.addSource(mRepository.postRequestCheckout(data), mCheckoutData::setValue)
    }

    fun requestPaytmDetails() {
        mPaytmData.addSource(mRepository.postPaytmDetailRequest(), mPaytmData::setValue)
    }

    val paytmDetails: LiveData<Resource<PaytmModel>> = mPaytmData

    fun postPaytmCallback(bundle: Bundle) {
        val data = objectMapper.createObjectNode()
        val keys = bundle.keySet()
        for (key in keys) {
            data.put(key, bundle[key].toString())
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

    private fun doPostPaytmCallback(data: ObjectNode, listener: UploadCallbacks) {
        RetrofitCallAsyncTask<ObjectNode>(listener).execute(mRepository.syncPostPaytmCallback(data))
    }

    val paytmCallbackData: LiveData<Resource<ObjectNode>> = mPaytmCallbackData

    val checkoutData: LiveData<Resource<CheckoutStatusModel>> = mCheckoutData

    fun fetchAvailablePromoCodes(restaurantId: Long) {
        val liveData = if (restaurantId > 0) {
            mSessionRepository.getRestaurantPromoCodes(restaurantId, true)
        } else mSessionRepository.allPromoCodes
        mPromoList.addSource(liveData, mPromoList::setValue)
    }

    val promoCodes: LiveData<Resource<List<PromoDetailModel>>>
        get() = mPromoList

    fun availPromoCode(code: String?) {
        val data = objectMapper.createObjectNode()
                .put("code", code)
        mSessionPromo.addSource(mRepository.postAvailPromoCode(data)) { sessionPromoModelResource ->
            if (sessionPromoModelResource == null) return@addSource
            mData.value = cloneResource(sessionPromoModelResource, data)
            if (sessionPromoModelResource.status === Resource.Status.SUCCESS && sessionPromoModelResource.data != null) mSessionPromo.value = sessionPromoModelResource
        }
    }

    fun removePromoCode() {
        mPromoDeletedData.addSource(mRepository.removePromoCode(), mPromoDeletedData::setValue)
    }

    fun cancelCheckoutRequest() {
        mSessionCancelCheckoutData.addSource(mRepository.removeCanceledCheckout(), mSessionCancelCheckoutData::setValue)
    }

    val sessionCancelCheckoutData: LiveData<Resource<ObjectNode>>
        get() = mSessionCancelCheckoutData

    val promoDeletedData: LiveData<Resource<ObjectNode?>?>
        get() = Transformations.map(mPromoDeletedData) { input ->
            if (input != null && input.status === Resource.Status.SUCCESS) {
                mSessionPromo.value = errorNotFound("Not Found")
            }
            input
        }

    fun fetchSessionAppliedPromo() {
        mSessionPromo.addSource(mRepository.sessionAppliedPromo, mSessionPromo::setValue)
    }

    val sessionAppliedPromo: LiveData<Resource<SessionPromoModel?>>
        get() = Transformations.map(mSessionPromo) { input ->
            if (input == null) return@map null
            if (input.status === Resource.Status.SUCCESS && input.data != null)
                updateOfferInInvoice(input.data.code, input.data.offerAmount)
            else if (input.status === Resource.Status.ERROR_NOT_FOUND)
                updateOfferInInvoice(null, null)
            input
        }

    private fun updateOfferInInvoice(code: String?, offerAmount: Double?) {
        val listResource = mInvoiceData.value
        if (listResource?.data == null) return
        val sessionBillModel = listResource.data.bill
        sessionBillModel.promo = code
        sessionBillModel.offers = offerAmount
        mInvoiceData.value = cloneResource(listResource, listResource.data)
    }

    val requestedCheckout: LiveData<Boolean?> = mIsRequestedCheckout

    val isRequestedCheckout: Boolean
        get() = requestedCheckout.value == true

    fun updateRequestCheckout(isCheckout: Boolean) {
        mIsRequestedCheckout.value = isCheckout
    }

    val isOfferApplied: Boolean
        get() {
            val resource = mSessionPromo.value
            return resource?.data != null
        }

    fun showedSessionBenefits() {
        isSessionBenefitsShown = true
    }

    override fun updateResults() {
        fetchSessionInvoice()
        fetchSessionAppliedPromo()
    }
}