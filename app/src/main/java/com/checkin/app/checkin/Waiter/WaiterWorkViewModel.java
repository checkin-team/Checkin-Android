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
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.QRResultModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class WaiterWorkViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mShopTables = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<WaiterTableModel>>> mWaiterTables = new MediatorLiveData<>();
    private MediatorLiveData<Resource<QRResultModel>> mQrResult = new MediatorLiveData<>();

    public WaiterWorkViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public void fetchWaiterServedTables() {
        mWaiterTables.addSource(mWaiterRepository.getWaiterServedTables(), mWaiterTables::setValue);
    }

    public void fetchShopActiveTables(long shopId) {
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

    public void setupDummyData() {
        List<WaiterTableModel> tableModels = new ArrayList<>();
        tableModels.add(new WaiterTableModel(1, "Table 1"));
        tableModels.add(new WaiterTableModel(2, "Table 2"));
        mWaiterTables.setValue(Resource.success(tableModels));

        List<RestaurantTableModel> restaurantTableModels = new ArrayList<>();
        restaurantTableModels.add(new RestaurantTableModel(1, "Random", null, new EventBriefModel(1, SessionChatModel.CHAT_EVENT_TYPE.EVENT_CUSTOM_MESSAGE, "Message")));
        restaurantTableModels.add(new RestaurantTableModel(2, "Table 4", new BriefModel("1", "Alex", null), new EventBriefModel(1, SessionChatModel.CHAT_EVENT_TYPE.EVENT_HOST_ASSIGNED, "Assigned")));
        restaurantTableModels.add(new RestaurantTableModel(3, "Special", null, new EventBriefModel(1, SessionChatModel.CHAT_EVENT_TYPE.EVENT_CONCERN, "Concern")));
        mShopTables.setValue(Resource.success(restaurantTableModels));
    }
}