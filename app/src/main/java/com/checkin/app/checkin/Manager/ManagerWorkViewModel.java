package com.checkin.app.checkin.Manager;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerStatsModel;
import com.checkin.app.checkin.Session.Model.CheckoutStatusModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Session.Model.TableSessionModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class ManagerWorkViewModel extends BaseViewModel {
    private ManagerRepository mManagerRepository;
    private WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<List<RestaurantTableModel>>> mActiveTablesData = new MediatorLiveData<>();
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
        //mActiveTablesData.addSource(mWaiterRepository.getShopActiveTables(restaurantId), mActiveTablesData::setValue);
        mActiveTablesData.addSource(mWaiterRepository.getShopTables(restaurantId), mActiveTablesData::setValue);
    }

    public void fetchStatistics() {
        mStatsData.addSource(mManagerRepository.getManagerStats(mShopPk), mStatsData::setValue);
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getActiveTables() {
        return mActiveTablesData;
    }

    public void markSessionDone(long sessionId) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("payment_mode", "csh");
        mCheckoutData.addSource(mManagerRepository.manageSessionCheckout(sessionId, data), mCheckoutData::setValue);
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
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return -1;
        for (int i = 0; i < resource.data.size(); i++) {
            TableSessionModel tableSessionModel = resource.data.get(i).getTableSessionModel();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public RestaurantTableModel getTableWithPosition(int position) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return null;
        if (position >= resource.data.size())
            return null;
        return resource.data.get(position);
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.add(0, tableModel);
        mActiveTablesData.setValue(Resource.cloneResource(resource, resource.data));
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }

    public void updateRemoveTable(long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        int pos = -1;
        for (int i = 0; i < resource.data.size(); i++) {
            TableSessionModel tableSessionModel = resource.data.get(i).getTableSessionModel();
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            resource.data.remove(pos);
            mActiveTablesData.setValue(Resource.cloneResource(resource, resource.data));
        }
    }
}
