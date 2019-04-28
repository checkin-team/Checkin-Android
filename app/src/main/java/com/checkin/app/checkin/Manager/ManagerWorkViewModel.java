package com.checkin.app.checkin.Manager;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerStatsModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.CheckoutStatusModel;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Session.Model.TableSessionModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mTablesData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ManagerStatsModel>> mStatsData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<CheckoutStatusModel>> mCheckoutData = new MediatorLiveData<>();

    private long mShopPk;

    public ManagerWorkViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.getInstance(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    public void fetchActiveTables(long restaurantId) {
        mShopPk = restaurantId;
        mTablesData.addSource(mWaiterRepository.getShopTables(restaurantId, true), mTablesData::setValue);
    }

    public void fetchStatistics() {
        mStatsData.addSource(mManagerRepository.getManagerStats(mShopPk), mStatsData::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables() {
        return Transformations.map(mTablesData, input -> {
            if (input == null || input.data == null || input.status != Resource.Status.SUCCESS)
                return input;

            Collections.sort(input.data, (t1, t2) -> t2.getTableSession().getEvent().getTimestamp().compareTo(t1.getTableSession().getEvent().getTimestamp()));
            return Resource.cloneResource(input, input.data);
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
        if (resource == null || resource.data == null)
            return -1;
        for (int i = 0; i < resource.data.size(); i++) {
            TableSessionModel tableSessionModel = resource.data.get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public RestaurantTableModel getTableWithPosition(int position) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.data == null)
            return null;
        if (position >= resource.data.size())
            return null;
        return resource.data.get(position);
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.data == null)
            return;

        resource.data.add(0, tableModel);
        mTablesData.setValue(Resource.cloneResource(resource, resource.data));
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }

    public void updateRemoveTable(long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        int pos = -1;
        for (int i = 0; i < resource.data.size(); i++) {
            TableSessionModel tableSessionModel = resource.data.get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            resource.data.remove(pos);
            mTablesData.setValue(Resource.cloneResource(resource, resource.data));
        }
    }

    public void updateTable(long sessionPk, EventBriefModel event) {
        Resource<List<RestaurantTableModel>> listResource = mTablesData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        int pos = 0;
        for (int i = 0; i < listResource.data.size(); i++) {
            TableSessionModel tableSessionModel = listResource.data.get(i).getTableSession();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                pos = i;
                RestaurantTableModel table = listResource.data.get(pos);
                if (table != null) {
                    tableSessionModel.setEvent(event);
                    if (event.getType() == SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT)
                        tableSessionModel.setRequestedCheckout(true);
                    table.addEventCount();
                    listResource.data.remove(pos);
                    listResource.data.add(0,table);
                    mTablesData.setValue(Resource.cloneResource(listResource, listResource.data));
                }
            }
        }


    }
}
