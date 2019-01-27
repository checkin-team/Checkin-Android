package com.checkin.app.checkin.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;

import java.util.List;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mActiveTablesData = new MediatorLiveData<>();

    private long mShopPk;

    public ManagerWorkViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.getInstance(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    public void fetchActiveTables(long restaurantId) {
        mShopPk = restaurantId;
        mActiveTablesData.addSource(mWaiterRepository.getShopActiveTables(restaurantId), mActiveTablesData::setValue);
    }

    public void fetchStatistics() {

    }

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables(){
        return mActiveTablesData;
    }

    @Override
    public void updateResults() {

    }
}
