package com.checkin.app.checkin.Cook;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Cook.Model.CookTableModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.utility.SourceMappedLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookWorkViewModel extends BaseViewModel {
    private CookRepository mCookRepository;

    private SourceMappedLiveData<Resource<List<CookTableModel>>> mTablesData = createNetworkLiveData();

    private long mShopPk;

    public CookWorkViewModel(@NonNull Application application) {
        super(application);
        mCookRepository = CookRepository.getInstance(application);
    }

    public void fetchActiveTables(long restaurantId) {
        mShopPk = restaurantId;
        mTablesData.addSource(mCookRepository.getActiveTables(restaurantId), mTablesData::setValue);
    }

    public LiveData<Resource<List<CookTableModel>>> getActiveTables() {
        return Transformations.map(mTablesData, input -> {
            if (input == null || input.getData() == null || input.getStatus() != Resource.Status.SUCCESS)
                return input;

            List<CookTableModel> result = new ArrayList<>(input.getData());

            Collections.sort(result, (t1, t2) -> {
                if (!t1.getEventCount().equals(t2.getEventCount()))
                    return t2.getEventCount().compareTo(t1.getEventCount());
                else if (!t2.getPendingOrders().equals(t1.getPendingOrders())) {
                    return t2.getPendingOrders().compareTo(t1.getPendingOrders());
                } else {
                    return t2.getCreated().compareTo(t1.getCreated());
                }
            });
            return Resource.Companion.cloneResource(input, result);
        });
    }

    public long getShopPk() {
        return mShopPk;
    }

    public int getTablePositionWithPk(long sessionPk) {
        Resource<List<CookTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return -1;
        for (int i = 0; i < resource.getData().size(); i++) {
            CookTableModel tableSessionModel = resource.getData().get(i);
            if (tableSessionModel != null && tableSessionModel.getPk() == sessionPk) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public CookTableModel getTableWithPosition(int position) {
        Resource<List<CookTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return null;
        if (position >= resource.getData().size())
            return null;
        return resource.getData().get(position);
    }

    public void addRestaurantTable(CookTableModel tableModel) {
        Resource<List<CookTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return;

        resource.getData().add(0, tableModel);
        mTablesData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    @Override
    public void updateResults() {
        fetchActiveTables(mShopPk);
    }

    public void updateRemoveTable(long sessionPk) {
        Resource<List<CookTableModel>> resource = mTablesData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0; i < resource.getData().size(); i++) {
            CookTableModel tableSessionModel = resource.getData().get(i);
            if (tableSessionModel.getPk() == sessionPk) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            resource.getData().remove(pos);
            mTablesData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
        }
    }

    public void updateTable(long sessionPk, boolean isNewOrder) {
        Resource<List<CookTableModel>> listResource = mTablesData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        for (int i = 0; i < listResource.getData().size(); i++) {
            CookTableModel tableSessionModel = listResource.getData().get(i);
            if (tableSessionModel.getPk() == sessionPk) {
                if (isNewOrder)
                    tableSessionModel.setPendingOrders(tableSessionModel.getPendingOrders() + 1);
                tableSessionModel.increaseEventCount();
                listResource.getData().remove(i);
                listResource.getData().add(0, tableSessionModel);
                mTablesData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
                break;
            }
        }
    }
}
