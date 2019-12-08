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
        ObjectNode requestJson = Converters.INSTANCE.getObjectMapper().createObjectNode();
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
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","4.4","Lorem ipsum","3 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","4.9","Lorem ipsum","5 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","3","Lorem ipsum","30 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","4.4","Lorem ipsum","3.3 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","2","Lorem ipsum","6 min away"));

        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","5.0","Lorem ipsum","45 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","2.2","Lorem ipsum","2 min away"));
        data.add(new NearbyRestaurantModel("THC", "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg"
                ,"Pure Veg American Burgers","2.3","Lorem ipsum","1 min away"));

        return new MutableLiveData<>(Resource.Companion.success(data));
    }
}
