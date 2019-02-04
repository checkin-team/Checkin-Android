package com.checkin.app.checkin.Manager.Model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.ManagerRepository;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;

import java.util.List;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mActiveTablesData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ManagerStatsModel>> mStatsData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mDetailData = new MediatorLiveData<>();

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
        mStatsData.addSource(mManagerRepository.getManagerStats(mShopPk), mStatsData::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables() {
        return mActiveTablesData;
    }

    public void markSessionDone(long sessionId) {
        mDetailData.addSource(mManagerRepository.postSessionCheckout(sessionId), mDetailData::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getDetailData() {
        return mDetailData;
    }

    public LiveData<Resource<ManagerStatsModel>> getRestaurantStatistics() {
        return mStatsData;
    }

    public long getShopPk() {
        return mShopPk;
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }
}
