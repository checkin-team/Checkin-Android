package com.checkin.app.checkin.Home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.SessionRepository;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.SessionBasicModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModel {
    private SessionRepository mSessionRepository;

    private SourceMappedLiveData<Resource<QRResultModel>> mQrResult = createNetworkLiveData();
    private SourceMappedLiveData<Resource<SessionBasicModel>> mSessionStatus = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mCancelDineInRequest = createNetworkLiveData();

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

    public void cancelUserWaitingDineIn() {
        mCancelDineInRequest.addSource(mSessionRepository.cancelSessionJoinRequest(), mCancelDineInRequest::setValue);
    }

    public LiveData<Resource<ObjectNode>> getCancelDineInData() {
        return mCancelDineInRequest;
    }

    public LiveData<Resource<List<NearbyRestaurantModel>>> getNearbyRestaurantData() {
        List<NearbyRestaurantModel> data = new ArrayList<>();
        data.add(new NearbyRestaurantModel("THC", R.drawable.first_banner));
        data.add(new NearbyRestaurantModel("THC", R.drawable.second_banner));
        data.add(new NearbyRestaurantModel("THC", R.drawable.third_banner));
        data.add(new NearbyRestaurantModel("THC", R.drawable.second_banner));
        data.add(new NearbyRestaurantModel("THC", R.drawable.third_banner));

        data.add(new NearbyRestaurantModel("THC", R.drawable.first_banner));

        return new MutableLiveData<>(Resource.Companion.success(data));
    }
}
