package com.checkin.app.checkin.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;

import java.util.ArrayList;
import java.util.List;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mActiveTablesData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<RestaurantStaticsModel>> mRestaurantStaticsData = new MediatorLiveData<>();

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

    void fetchStatistics() {
        mRestaurantStaticsData.addSource(mManagerRepository.getRestaurantStaticsById(String.valueOf(mShopPk)),mRestaurantStaticsData::setValue);
    }

    /*void dummyData(){

        RestaurantStaticsModel staticsModel = new RestaurantStaticsModel();
        staticsModel.setAvgServingTime("10.16 Min");
        staticsModel.setAvgSessionTime("50.02 Min");
        staticsModel.setDaysRevenue("₹ 3,320");
        staticsModel.setWeeksRevenue("₹ 19,120");

        List<RestaurantStaticsModel.TrendingOrder> mList = new ArrayList<>();

        for (int i = 0; i < 3; i++){
            RestaurantStaticsModel.TrendingOrder trendingOrder = new RestaurantStaticsModel.TrendingOrder();

            RestaurantItemModel itemModel = new RestaurantItemModel();
            itemModel.setPk(1);
            itemModel.setName("Kadaai Egg");
            itemModel.setVegetarian(true);

            trendingOrder.setItem(itemModel);
            trendingOrder.setRevenueGenerated("60.8 % revenue contribution");

            mList.add(trendingOrder);
        }

        staticsModel.setTrendingOrders(mList);

        mRestaurantStaticsData.setValue(Resource.success(staticsModel));
    }*/

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables(){
        return mActiveTablesData;
    }

    LiveData<Resource<RestaurantStaticsModel>> getRestaurantStatics(){
        return mRestaurantStaticsData;
    }

    @Override
    public void updateResults() {

    }
}
