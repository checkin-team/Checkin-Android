package com.checkin.app.checkin.Waiter;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.Model.QRResultModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class WaiterWorkViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mShopTables = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<WaiterTableModel>>> mWaiterTables = new MediatorLiveData<>();
    private MediatorLiveData<Resource<QRResultModel>> mQrResult = new MediatorLiveData<>();
    private MediatorLiveData<Resource<WaiterStatsModel>> mWaiterStats = new MediatorLiveData<>();

    private long mShopPk;

    public WaiterWorkViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchShopActiveTables(mShopPk);
        fetchWaiterStats();
    }

    public void fetchWaiterServedTables() {
        mWaiterTables.addSource(mWaiterRepository.getWaiterServedTables(), mWaiterTables::setValue);
    }

    public void fetchWaiterStats() {
        mWaiterStats.addSource(mWaiterRepository.getWaiterStats(mShopPk), mWaiterStats::setValue);
    }

    public void fetchShopActiveTables(long shopId) {
        mShopPk = shopId;
        mShopTables.addSource(mWaiterRepository.getShopActiveTables(shopId), mShopTables::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopAssignedTables() {
        return Transformations.map(mShopTables, input -> {
            if (input == null || input.data == null || input.status != Status.SUCCESS)
                return input;

            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0, length = input.data.size(); i < length; i++) {
                RestaurantTableModel tableModel = input.data.get(i);
                if (tableModel.getHost() != null)
                    result.add(tableModel);
            }
            return Resource.cloneResource(input, result);
        });
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopUnassignedTables() {
        return Transformations.map(mShopTables, input -> {
            if (input == null || input.data == null || input.status != Status.SUCCESS)
                return input;


            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0, length = input.data.size(); i < length; i++) {
                RestaurantTableModel tableModel = input.data.get(i);
                if (tableModel.getHost() == null)
                    result.add(tableModel);
            }
            return Resource.cloneResource(input, result);
        });
    }

    public LiveData<Resource<WaiterStatsModel>> getWaiterStats() {
        return mWaiterStats;
    }

    public LiveData<Resource<List<WaiterTableModel>>> getWaiterTables() {
        return mWaiterTables;
    }

    public LiveData<Resource<QRResultModel>> getQrResult() {
        return mQrResult;
    }

    public void processQr(String data) {
        ObjectNode requestJson = Converters.objectMapper.createObjectNode();
        requestJson.put("data", data);
        mQrResult.addSource(mWaiterRepository.newWaiterSession(requestJson), mQrResult::setValue);
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mShopTables.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.add(tableModel);
        mShopTables.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void updateShopTable(long sessionPk, BriefModel host) {
        Resource<List<RestaurantTableModel>> resource = mShopTables.getValue();
        if (resource == null || resource.data == null)
            return;
        for (RestaurantTableModel table : resource.data) {
            if (table.getPk() == sessionPk) {
                table.setHost(host);
                break;
            }
        }
        mShopTables.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void markSessionEnd(long sessionPk) {
        Resource<List<RestaurantTableModel>> shopTableResource = mShopTables.getValue();
        if (shopTableResource != null && shopTableResource.data != null) {
            int pos = -1;
            for (int i = 0, length = shopTableResource.data.size(); i < length; i++) {
                if (shopTableResource.data.get(i).getPk() == sessionPk)
                    pos = i;
                if (pos > -1) {
                    shopTableResource.data.remove(pos);
                    mShopTables.setValue(Resource.cloneResource(shopTableResource, shopTableResource.data));
                }
            }
        }

        Resource<List<WaiterTableModel>> waiterTableResource = mWaiterTables.getValue();
        if (waiterTableResource != null && waiterTableResource.data != null) {
            int pos = -1;
            for (int i = 0, length = waiterTableResource.data.size(); i < length; i++) {
                if (waiterTableResource.data.get(i).getPk() == sessionPk)
                    pos = i;
                if (pos > -1) {
                    waiterTableResource.data.remove(pos);
                    mWaiterTables.setValue(Resource.cloneResource(waiterTableResource, waiterTableResource.data));
                }
            }
        }
    }
}