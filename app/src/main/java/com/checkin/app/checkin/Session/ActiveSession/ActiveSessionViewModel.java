package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Bhavik Patel on 05/08/2018.
 */

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();

    private String mShopPk;

    ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getActiveSessionDetail();
    }

    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
        return mSessionData;
    }

    public LiveData<List<OrderedItemModel>> getOrderedItems() {
        return Transformations.map(getActiveSessionDetail(), input -> {
            List<OrderedItemModel> orderedItems = null;
            if (input != null && input.data != null) {
                orderedItems = input.data.getOrderedItems();
            }
            return orderedItems;
        });
    }

    public void cancelOrders(String pk) { }

    public void checkoutSession() {
        Log.e("Session", "Checked out");
    }

    public void addMembers(List<Long> ids) {
        ArrayNode value = Converters.objectMapper.valueToTree(ids);
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.putArray("users").addAll(value);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public void setShopPk(String shopPk) {
        mShopPk = shopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }
}
