package com.checkin.app.checkin.session.activesession;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitCallAsyncTask;
import com.checkin.app.checkin.Misc.paytm.PaytmModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.PromoDetailModel;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Set;

public class ActiveSessionInvoiceViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private SourceMappedLiveData<Resource<SessionInvoiceModel>> mInvoiceData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<CheckoutStatusModel>> mCheckoutData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<PaytmModel>> mPaytmData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<PromoDetailModel>>> mPromoList = createNetworkLiveData();
    private SourceMappedLiveData<Resource<SessionPromoModel>> mSessionPromo = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mPromoDeletedData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mPaytmCallbackData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mSessionCancelCheckoutData = createNetworkLiveData();
    private MutableLiveData<Boolean> mIsRequestedCheckout = new MutableLiveData<>(false);

    private boolean mSessionBenefitsSet = false;
    private boolean mSessionPromoInvalid = false;

    public ActiveSessionInvoiceViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    public LiveData<Resource<SessionInvoiceModel>> getSessionInvoice() {
        return mInvoiceData;
    }

    public void fetchSessionInvoice() {
        mInvoiceData.addSource(mRepository.getActiveSessionInvoice(), mInvoiceData::setValue);
    }

    public void requestCheckout(double tip, ShopModel.PAYMENT_MODE paymentMode, boolean override) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode()
                .put("tip", tip)
                .put("payment_mode", paymentMode.tag)
                .put("override", override);
        mCheckoutData.addSource(mRepository.postRequestCheckout(data), mCheckoutData::setValue);
    }

    public void requestPaytmDetails() {
        mPaytmData.addSource(mRepository.postPaytmDetailRequest(), mPaytmData::setValue);
    }

    public LiveData<Resource<PaytmModel>> getPaytmDetails() {
        return mPaytmData;
    }

    public void postPaytmCallback(Bundle bundle) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            data.put(key, String.valueOf(bundle.get(key)));
        }
        ProgressRequestBody.UploadCallbacks listener = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                mPaytmCallbackData.postValue(Resource.Companion.loading(null));
            }

            @Override
            public void onSuccess() {
                mPaytmCallbackData.postValue(Resource.Companion.success(null));
            }

            @Override
            public void onFailure() {
                mPaytmCallbackData.postValue(Resource.Companion.error("Sorry, but PayTM transaction failed", null));
            }
        };
        doPostPaytmCallback(data, listener);
    }

    private void doPostPaytmCallback(ObjectNode data, ProgressRequestBody.UploadCallbacks listener) {
        new RetrofitCallAsyncTask<ObjectNode>(listener).execute(mRepository.synchPostPaytmCallback(data));
    }

    public LiveData<Resource<ObjectNode>> getPaytmCallbackData() {
        return mPaytmCallbackData;
    }

    public LiveData<Resource<CheckoutStatusModel>> getCheckoutData() {
        return mCheckoutData;
    }

    public void fetchAvailablePromoCodes() {
        mPromoList.addSource(mRepository.getAvailablePromoCodes(), mPromoList::setValue);
    }

    public LiveData<Resource<List<PromoDetailModel>>> getPromoCodes() {
        return mPromoList;
    }

    public void availPromoCode(String code) {
        final ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode()
                .put("code", code);
        mSessionPromo.addSource(mRepository.postAvailPromoCode(data), sessionPromoModelResource -> {
            if (sessionPromoModelResource == null)
                return;
            getMData().setValue(Resource.Companion.cloneResource(sessionPromoModelResource, data));
            if (sessionPromoModelResource.getStatus() == Resource.Status.SUCCESS && sessionPromoModelResource.getData() != null)
                mSessionPromo.setValue(sessionPromoModelResource);
        });
    }

    public void removePromoCode() {
        mPromoDeletedData.addSource(mRepository.removePromoCode(), mPromoDeletedData::setValue);
    }

    public void cancelCheckoutRequest() {
        mSessionCancelCheckoutData.addSource(mRepository.removeCanceledCheckout(), mSessionCancelCheckoutData::setValue);
    }

    public LiveData<Resource<ObjectNode>> getSessionCancelCheckoutData() {
        return mSessionCancelCheckoutData;
    }

    public LiveData<Resource<ObjectNode>> getPromoDeletedData() {
        return Transformations.map(mPromoDeletedData, input -> {
            if (input != null && input.getStatus() == Resource.Status.SUCCESS) {
                mSessionPromo.setValue(Resource.Companion.errorNotFound("Not Found"));
            }
            return input;
        });
    }

    public void fetchSessionAppliedPromo() {
        mSessionPromo.addSource(mRepository.getSessionAppliedPromo(), mSessionPromo::setValue);
    }

    public LiveData<Resource<SessionPromoModel>> getSessionAppliedPromo() {
        return Transformations.map(mSessionPromo, input -> {
            if (input == null)
                return null;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null)
                updateOfferInInvoice(input.getData().getCode(), input.getData().getOfferAmount());
            else if (input.getStatus() == Resource.Status.ERROR_NOT_FOUND)
                updateOfferInInvoice(null, null);
            return input;
        });
    }

    private void updateOfferInInvoice(String code, Double offerAmount) {
        Resource<SessionInvoiceModel> listResource = mInvoiceData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        SessionBillModel sessionBillModel = listResource.getData().getBill();
        sessionBillModel.setPromo(code);
        sessionBillModel.setOffers(offerAmount);
        mInvoiceData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public LiveData<Boolean> getRequestedCheckout() {
        return mIsRequestedCheckout;
    }

    public void updateRequestCheckout(boolean isCheckout) {
        mIsRequestedCheckout.setValue(isCheckout);
    }

    public boolean isRequestedCheckout() {
        if (mIsRequestedCheckout.getValue() != null)
            return mIsRequestedCheckout.getValue();
        return false;
    }

    public boolean isOfferApplied() {
        Resource<SessionPromoModel> resource = mSessionPromo.getValue();
        return resource != null && resource.getData() != null;
    }

    public void showedSessionBenefits() {
        mSessionBenefitsSet = true;
    }

    public boolean isSessionBenefitsShown() {
        return mSessionBenefitsSet;
    }

    public boolean isSessionPromoInvalid() {
        return mSessionPromoInvalid;
    }

    public void setSessionPromoInvalid(boolean value) {
        mSessionPromoInvalid = value;
    }

    @Override
    public void updateResults() {
        fetchSessionInvoice();
        fetchSessionAppliedPromo();
    }
}
