package com.checkin.app.checkin.Manager.Model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.ManagerRepository;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
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

    public int getTablePositionWithPk(long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return -1;
        for (int i = 0; i < resource.data.size(); i++) {
            if (resource.data.get(i).getPk() == sessionPk) {
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

    public void updateEventCount(long sessionPk, EventBriefModel event) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        for (RestaurantTableModel table : resource.data) {
            if (table.getPk() == sessionPk) {
                table.setEvent(event);
                break;
            }
        }
    }

    public void addRestaurantTable(RestaurantTableModel tableModel) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.add(tableModel);
        mActiveTablesData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void updateShopTableHost(long sessionPk, BriefModel host) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        for (RestaurantTableModel table : resource.data) {
            if (table.getPk() == sessionPk) {
                table.setHost(host);
                break;
            }
        }
        mActiveTablesData.setValue(Resource.cloneResource(resource, resource.data));
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }

    public void updateRemoveTable(Long sessionPk) {
        Resource<List<RestaurantTableModel>> resource = mActiveTablesData.getValue();
        if (resource == null || resource.data == null)
            return;
        int pos = -1;
        for (int i = 0; i < resource.data.size(); i++) {
            if (resource.data.get(i).getPk() == sessionPk) {
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
