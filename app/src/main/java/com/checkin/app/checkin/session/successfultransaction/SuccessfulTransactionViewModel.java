package com.checkin.app.checkin.session.successfultransaction;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailModel;
import com.checkin.app.checkin.session.SessionRepository;
import com.checkin.app.checkin.session.activesession.ActiveSessionRepository;
import com.checkin.app.checkin.session.model.SessionSuccessfulTransactionModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class SuccessfulTransactionViewModel extends BaseViewModel {
    private final SessionRepository mRepository;

    private MediatorLiveData<Resource<SessionSuccessfulTransactionModel>> mBriefData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ShopSessionDetailModel>> mDetailData = new MediatorLiveData<>();

    public SuccessfulTransactionViewModel(@NonNull Application application) {
        super(application);
        mRepository = SessionRepository.getInstance(application);
    }


    public void fetchSessionSuccessfulTransaction(long sessionId) {
        mBriefData.addSource(mRepository.getUserSessionBriefDetail(sessionId), mBriefData::setValue);
    }

    public void fetchUserSessionDetail(long sessionId) {
        mDetailData.addSource(mRepository.getUserSessionDetail(sessionId), mDetailData::setValue);
    }

    public LiveData<Resource<SessionSuccessfulTransactionModel>> getUserSessionBriefData() {
        return mBriefData;
    }

    public LiveData<Resource<ShopSessionDetailModel>> getUserSessionDetail() {
        return mDetailData;
    }

    @Override
    public void updateResults() {

    }
}
