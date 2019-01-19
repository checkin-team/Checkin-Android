package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Shop.ShopModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;
    private final SessionRepository mSessionRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();

    private int mShopPk = -1, mSessionPk = -1;

    ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
        mSessionRepository = SessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchActiveSessionDetail();
    }

    public void fetchActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
    }

    public LiveData<Resource<ActiveSessionModel>> getSessionData() {
        return mSessionData;
    }

    public void addMembers(long id) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("user_id", id);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public void sendSelfPresence(boolean isPublic) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("is_public", isPublic);
        mData.addSource(mRepository.putSelfPresence(data), mData::setValue);
    }

    public LiveData<Resource<SessionInvoiceModel>> getSessionInvoice() {
        return mInvoiceData;
    }

    public void fetchSessionInvoice() {
        mInvoiceData.addSource(mSessionRepository.getSessionInvoiceDetail(mSessionPk), mInvoiceData::setValue);
    }

    public void requestCheckout(double tip, ShopModel.PAYMENT_MODE paymentMode) {
        ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("tip", tip)
                .put("payment_mode", paymentMode.tag);
        mData.addSource(mRepository.postRequestCheckout(data), mData::setValue);
    }

    public void setShopPk(int shopPk) {
        mShopPk = shopPk;
    }

    public int getShopPk() {
        return mShopPk;
    }

    public void setSessionPk(int sessionPk) {
        mSessionPk = sessionPk;
    }

    public int getSessionPk() {
        return mSessionPk;
    }
}
