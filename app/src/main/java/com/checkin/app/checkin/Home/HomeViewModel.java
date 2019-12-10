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
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                5,4.2,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                12,4.0,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                14,3.9,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                3,5,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                400,5,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                40,4,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                120,4,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                180,4,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));
        data.add(new NearbyRestaurantModel("Grey Orange","+92323232",getLogo(),getCuisines(),"Nice World","Lavel road",47,new GeolocationClass(134,423),
                300,4,getCuisines(),new OfferClass("45%",true,"COUPON50","Exclusive")));


        return new MutableLiveData<>(Resource.Companion.success(data));
    }
    public List<String> getCuisines(){
        List<String>strings=new ArrayList<>();
        strings.add("Pure Veg");
        strings.add("American Burgers");
        return strings;


    }
    public String getLogo(){
        return "https://www.washingtonpost.com/resizer/Kv8laiHtDM2ZOMNV4eP3lUTN8HA=/767x0/smart/arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/2KL6JYQYH4I6REYMIWBYVUGXPI.jpg";
    }
}
