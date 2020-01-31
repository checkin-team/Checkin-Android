package com.checkin.app.checkin.Waiter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.Converters;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.data.resource.Resource.Status;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.session.models.QRResultModel;
import com.checkin.app.checkin.session.models.RestaurantTableModel;
import com.checkin.app.checkin.session.models.TableSessionModel;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class WaiterWorkViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;

    private SourceMappedLiveData<Resource<List<RestaurantTableModel>>> mShopTables = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<WaiterTableModel>>> mWaiterTables = createNetworkLiveData();
    private SourceMappedLiveData<Resource<QRResultModel>> mQrResult = createNetworkLiveData();
    private SourceMappedLiveData<Resource<WaiterStatsModel>> mWaiterStats = createNetworkLiveData();

    private long mShopPk;

    public WaiterWorkViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchShopTables(mShopPk);
        fetchWaiterStats();
    }

    public void fetchWaiterServedTables() {
        mWaiterTables.addSource(mWaiterRepository.getWaiterServedTables(), mWaiterTables::setValue);
    }

    public void fetchWaiterStats() {
        mWaiterStats.addSource(mWaiterRepository.getWaiterStats(mShopPk), mWaiterStats::setValue);
    }

    public void fetchShopTables(long shopId) {
        mShopPk = shopId;
        mShopTables.addSource(mWaiterRepository.getShopTables(shopId, false), mShopTables::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopAssignedTables() {
        return Transformations.map(mShopTables, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Status.SUCCESS)
                return input;

            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0, length = input.getData().size(); i < length; i++) {
                RestaurantTableModel tableModel = input.getData().get(i);
                if (tableModel.isSessionActive() && tableModel.getTableSession().hasHost())
                    result.add(tableModel);
            }
            return Resource.Companion.cloneResource(input, result);
        });
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopUnassignedTables() {
        return Transformations.map(mShopTables, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Status.SUCCESS)
                return input;


            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0, length = input.getData().size(); i < length; i++) {
                RestaurantTableModel tableModel = input.getData().get(i);
                if (tableModel.isSessionActive() && !tableModel.getTableSession().hasHost())
                    result.add(tableModel);
            }
            return Resource.Companion.cloneResource(input, result);
        });
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopInactiveTables() {
        return Transformations.map(mShopTables, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Status.SUCCESS)
                return input;


            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0, length = input.getData().size(); i < length; i++) {
                RestaurantTableModel tableModel = input.getData().get(i);
                if (!tableModel.isSessionActive())
                    result.add(tableModel);
            }
            return Resource.Companion.cloneResource(input, result);
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

    public void processQrPk(long qrPk) {
        ObjectNode requestJson = Converters.INSTANCE.getObjectMapper().createObjectNode();
        requestJson.put("qr", qrPk);
        mQrResult.addSource(mWaiterRepository.newWaiterSession(requestJson), mQrResult::setValue);
    }

    public void processQr(String data) {
        ObjectNode requestJson = Converters.INSTANCE.getObjectMapper().createObjectNode();
        requestJson.put("data", data);
        mQrResult.addSource(mWaiterRepository.newWaiterSession(requestJson), mQrResult::setValue);
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mShopTables.getValue();
        if (resource == null || resource.getData() == null)
            return;

        if (resource.getData().contains(tableModel))
            return;
        for (RestaurantTableModel table : resource.getData()) {
            if (tableModel.getQrPk() == RestaurantTableModel.NO_QR_ID ? tableModel.getTable().equals(table.getTable()) : table.getQrPk() == tableModel.getQrPk()) {
                table.setTableSession(tableModel.getTableSession());
                break;
            }
        }
        mShopTables.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void updateShopTable(long sessionPk, BriefModel host) {
        Resource<List<RestaurantTableModel>> resource = mShopTables.getValue();
        if (resource == null || resource.getData() == null)
            return;
        for (RestaurantTableModel table : resource.getData()) {
            if (table.isSessionActive() && table.getTableSession().getPk() == sessionPk) {
                table.getTableSession().setHost(host);
                break;
            }
        }
        mShopTables.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void markSessionEnd(long sessionPk) {
        Resource<List<RestaurantTableModel>> shopTableResource = mShopTables.getValue();
        if (shopTableResource != null && shopTableResource.getData() != null) {
            for (int i = 0, length = shopTableResource.getData().size(); i < length; i++) {
                TableSessionModel sessionModel = shopTableResource.getData().get(i).getTableSession();
                if (sessionModel != null && sessionModel.getPk() == sessionPk) {
                    shopTableResource.getData().get(i).setTableSession(null);
                    mShopTables.setValue(Resource.Companion.cloneResource(shopTableResource, shopTableResource.getData()));
                    break;
                }
            }
        }

        Resource<List<WaiterTableModel>> waiterTableResource = mWaiterTables.getValue();
        if (waiterTableResource != null && waiterTableResource.getData() != null) {
            for (int i = 0, length = waiterTableResource.getData().size(); i < length; i++) {
                if (waiterTableResource.getData().get(i).getPk() == sessionPk) {
                    waiterTableResource.getData().remove(i);
                    mWaiterTables.setValue(Resource.Companion.cloneResource(waiterTableResource, waiterTableResource.getData()));
                    break;
                }
            }
        }
    }

    public void addWaiterTable(WaiterTableModel tableModel) {
        Resource<List<WaiterTableModel>> waiterTableResource = mWaiterTables.getValue();
        if (waiterTableResource == null || waiterTableResource.getData() == null)
            return;

        if (waiterTableResource.getData().contains(tableModel))
            return;

        waiterTableResource.getData().add(tableModel);
        mWaiterTables.setValue(Resource.Companion.cloneResource(waiterTableResource, waiterTableResource.getData()));
    }
}