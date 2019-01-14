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
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChatModel;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Bhavik Patel on 05/08/2018.
 */

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionViewOrdersModel>>> mOrdersData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ActiveSessionChatModel>>> mChatData = new MediatorLiveData<>();

    private String mShopPk;

    ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getActiveSessionDetail();
        getSessionOrdersData();
    }

    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
        return mSessionData;
    }

    public LiveData<Resource<List<SessionViewOrdersModel>>> getSessionOrdersData() {
        mOrdersData.addSource(mRepository.getSessionOrdersDetails(),mOrdersData::setValue);
        return mOrdersData;
    }

    public LiveData<Resource<List<ActiveSessionChatModel>>> getSessionChat() {
        mChatData.addSource(mRepository.getSessionChatDetail(),mChatData::setValue);
        return mChatData;
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

    public void addMembers(String ids) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("user_id",ids);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public void sendSelfPresence(boolean is_public) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("is_public",is_public);
        mData.addSource(mRepository.putSelfPresence(data), mData::setValue);
    }

    public void sendMessage(String msg) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("message",msg);
        mData.addSource(mRepository.postMessage(data), mData::setValue);
    }

    public void deleteSessionOrder(String order_id) {
        mData.addSource(mRepository.removeSessionOrder(order_id), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void setShopPk(String shopPk) {
        mShopPk = shopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }
}
