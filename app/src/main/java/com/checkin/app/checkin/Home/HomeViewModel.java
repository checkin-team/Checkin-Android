package com.checkin.app.checkin.Home;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.SessionBasicModel;
import com.checkin.app.checkin.session.SessionRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class HomeViewModel extends BaseViewModel {
    private SessionRepository mSessionRepository;

    private MediatorLiveData<Resource<QRResultModel>> mQrResult = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SessionBasicModel>> mSessionStatus = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ObjectNode>> mCancelDineInRequest = new MediatorLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mSessionRepository = SessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchSessionStatus();
    }

    public void processQr(String data) {
        ObjectNode requestJson = Converters.objectMapper.createObjectNode();
        requestJson.put("data", data);
        mQrResult.addSource(mSessionRepository.newCustomerSession(requestJson), mQrResult::setValue);
    }

    public void fetchSessionStatus() {
        mSessionStatus.addSource(mSessionRepository.getActiveSessionCheck(), mSessionStatus::setValue);
    }

    public LiveData<Resource<SessionBasicModel>> getSessionStatus() {
        return mSessionStatus;
    }

    public LiveData<Resource<QRResultModel>> getQrResult() {
        return mQrResult;
    }

    @Override
    protected void registerProblemHandlers() {
        mSessionStatus = registerProblemHandler(mSessionStatus);
        mQrResult = registerProblemHandler(mQrResult);

    }

    public void cancelUserWaitingDineIn(){
        mCancelDineInRequest.addSource(mSessionRepository.removeUserFromWaiting(), mCancelDineInRequest::setValue);
    }

    public LiveData<Resource<ObjectNode>> getCancelDineInData() {
        return mCancelDineInRequest;
    }
}
