package com.checkin.app.checkin.Manager;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerStatsModel;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.EventBriefModel;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.checkin.app.checkin.session.model.TableSessionModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private SourceMappedLiveData<Resource<List<RestaurantTableModel>>> mTablesData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ManagerStatsModel>> mStatsData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<CheckoutStatusModel>> mCheckoutData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<QRResultModel>> mQrResult = createNetworkLiveData();

    private long mShopPk;

    public ManagerWorkViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.getInstance(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    public void fetchActiveTables(long restaurantId) {
        mShopPk = restaurantId;
        mTablesData.addSource(mWaiterRepository.getShopTables(restaurantId, false), mTablesData::setValue);
    }

    public void fetchStatistics() {
        mStatsData.addSource(mManagerRepository.getManagerStats(mShopPk), mStatsData::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables() {
        return Transformations.map(mTablesData, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Resource.Status.SUCCESS)
                return input;

            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0; i < input.getData().size(); i++) {
                RestaurantTableModel tableModel = input.getData().get(i);
                if (tableModel.getTableSession() != null)
                    result.add(tableModel);
            }


            Collections.sort(result, (t1, t2) -> {
                if (t2.getTableSession().getEvent() != null && t1.getTableSession().getEvent() != null) {
                    return t2.getTableSession().getEvent().getTimestamp().compareTo(t1.getTableSession().getEvent().getTimestamp());
                } else {
                    return t2.getTableSession().getCreated().compareTo(t1.getTableSession().getCreated());
                }
            });
            return Resource.Companion.cloneResource(input, result);
        });
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getInactiveTables() {
        return Transformations.map(mTablesData, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Resource.Status.SUCCESS)
                return input;

            List<RestaurantTableModel> result = new ArrayList<>();
            for (int i = 0; i < input.getData().size(); i++) {
                RestaurantTableModel tableModel = input.getData().get(i);
                if (tableModel.getTableSession() == null)
                    result.add(tableModel);
            }
            return Resource.Companion.cloneResource(input, result);
        });
    }

    public void markSessionDone(long sessionId) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("payment_mode", "csh");
        mCheckoutData.addSource(mManagerRepository.manageSessionCheckout(sessionId), mCheckoutData::setValue);
    }

    public LiveData<Resource<CheckoutStatusModel>> getCheckoutData() {
        return mCheckoutData;
    }

    public LiveData<Resource<ManagerStatsModel>> getRestaurantStatistics() {
        return mStatsData;
    }

    public long getShopPk() {
        return mShopPk;
    }

    public int getTablePositionWithPk(long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return -1;
        for (int i = 0; i < resource.getData().size(); i++) {
            TableSessionModel tableSessionModel = resource.getData().get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public RestaurantTableModel getTableWithPosition(int position) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return null;
        if (position >= resource.getData().size())
            return null;
        return resource.getData().get(position);
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return;

        if (resource.getData().contains(tableModel)) return;
        resource.getData().add(0, tableModel);
        mTablesData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }

    public void updateRemoveTable(long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0; i < resource.getData().size(); i++) {
            TableSessionModel tableSessionModel = resource.getData().get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            resource.getData().remove(pos);
            mTablesData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
        }
    }

    public void updateTable(long sessionPk, EventBriefModel event) {
        Resource<List<RestaurantTableModel>> listResource = mTablesData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = 0;
        for (int i = 0; i < listResource.getData().size(); i++) {
            TableSessionModel tableSessionModel = listResource.getData().get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                pos = i;
                RestaurantTableModel table = listResource.getData().get(pos);
                if (table != null) {
                    tableSessionModel.setEvent(event);
                    if (event.getType() == SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT)
                        tableSessionModel.setRequestedCheckout(true);
                    table.addEventCount();
                    listResource.getData().remove(pos);
                    listResource.getData().add(0, table);
                    mTablesData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
                }
            }
        }
    }

    public void processQrPk(long qrPk) {
        ObjectNode requestJson = Converters.objectMapper.createObjectNode();
        requestJson.put("qr", qrPk);
        mQrResult.addSource(mManagerRepository.managerInitiateSession(requestJson), mQrResult::setValue);
    }

    public LiveData<Resource<QRResultModel>> getSessionInitiated() {
        return mQrResult;
    }
}
