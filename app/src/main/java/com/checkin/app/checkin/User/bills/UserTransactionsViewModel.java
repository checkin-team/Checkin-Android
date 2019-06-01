package com.checkin.app.checkin.User.bills;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.session.SessionRepository;

public class UserTransactionsViewModel extends BaseViewModel {
    private final SessionRepository mRepository;

    private MediatorLiveData<Resource<UserTransactionBriefModel>> mBriefData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<UserTransactionDetailsModel>> mDetailData = new MediatorLiveData<>();

    public UserTransactionsViewModel(@NonNull Application application) {
        super(application);
        mRepository = SessionRepository.getInstance(application);
    }

    @Override
    protected void registerProblemHandlers() {
        mBriefData = registerProblemHandler(mBriefData);
        mDetailData = registerProblemHandler(mDetailData);
    }

    public void fetchSessionSuccessfulTransaction(long sessionId) {
        mBriefData.addSource(mRepository.getUserSessionBriefDetail(sessionId), mBriefData::setValue);
    }

    public void fetchUserSessionDetail(long sessionId) {
        mDetailData.addSource(mRepository.getUserSessionDetail(sessionId), mDetailData::setValue);
    }

    public LiveData<Resource<UserTransactionBriefModel>> getUserSessionBriefData() {
        return mBriefData;
    }

    public LiveData<Resource<UserTransactionDetailsModel>> getUserSessionDetail() {
        return mDetailData;
    }

    @Override
    public void updateResults() {

    }
}
