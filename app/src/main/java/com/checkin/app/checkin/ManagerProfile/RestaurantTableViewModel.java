package com.checkin.app.checkin.ManagerProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;

import java.util.List;

public class RestaurantTableViewModel extends BaseViewModel {

    private RestaurantTableRepository mRestaurantTableRepository;
    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mResourceMediatorLiveData = new MediatorLiveData<>();

    public RestaurantTableViewModel(@NonNull Application application) {
        super(application);
        mRestaurantTableRepository = RestaurantTableRepository.getInstance(application);
    }

    void getRestaurantTableById(String restaurantId){
        LiveData<Resource<List<RestaurantTableModel>>> mLiveData = mRestaurantTableRepository.getRestaurantTableById(restaurantId);
        mResourceMediatorLiveData.addSource(mLiveData,mResourceMediatorLiveData::setValue);
    }

    LiveData<Resource<List<RestaurantTableModel>>> getRestaurantTableModel(){
        return mResourceMediatorLiveData;
    }

    @Override
    public void updateResults() {

    }
}
