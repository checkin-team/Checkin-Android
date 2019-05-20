package com.checkin.app.checkin.session.activesession;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.paytm.PaytmModel;
import com.checkin.app.checkin.Shop.ShopModel;
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

    private MediatorLiveData<Resource<SessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<CheckoutStatusModel>> mCheckoutData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<PaytmModel>> mPaytmData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<PromoDetailModel>>> mPromoList = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SessionPromoModel>> mSessionPromo = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mIsRequestedCheckout = new MutableLiveData<>(false);

    private boolean mSessionBenefitsSet = false;

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
        ObjectNode data = Converters.objectMapper.createObjectNode()
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
        ObjectNode data = Converters.objectMapper.createObjectNode();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            data.put(key, String.valueOf(bundle.get(key)));
        }
        mData.addSource(mRepository.postPaytmResult(data), mData::setValue);
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
        final ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("code", code);
        mSessionPromo.addSource(mRepository.postAvailPromoCode(data), sessionPromoModelResource -> {
            if (sessionPromoModelResource == null)
                return;
            mData.setValue(Resource.cloneResource(sessionPromoModelResource, data));
            if (sessionPromoModelResource.status == Resource.Status.SUCCESS && sessionPromoModelResource.data != null)
                mSessionPromo.setValue(sessionPromoModelResource);
        });
    }

    public void removePromoCode() {
        mData.addSource(mRepository.removePromoCode(), mData::setValue);
    }

    public void fetchSessionAppliedPromo() {
        mSessionPromo.addSource(mRepository.getSessionAppliedPromo(), mSessionPromo::setValue);
    }

    public LiveData<Resource<SessionPromoModel>> getSessionAppliedPromo() {
        return Transformations.map(mSessionPromo, input -> {
            if (input != null && input.data != null)
                updateOfferInInvoice(input.data.getCode(), input.data.getOfferAmount());
            return input;
        });
    }

    private void updateOfferInInvoice(String code, Double offerAmount) {
        Resource<SessionInvoiceModel> listResource = mInvoiceData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        SessionBillModel sessionBillModel = listResource.data.getBill();
        sessionBillModel.setPromo(code);
        sessionBillModel.setOffers(offerAmount);
        mInvoiceData.setValue(Resource.cloneResource(listResource, listResource.data));
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
        return resource != null && resource.data != null;
    }

    public void showedSessionBenefits() {
        mSessionBenefitsSet = true;
    }

    public boolean isSessionBenefitsShown() {
        return mSessionBenefitsSet;
    }

    @Override
    public void updateResults() {
        fetchSessionInvoice();
        fetchSessionAppliedPromo();
    }
}
