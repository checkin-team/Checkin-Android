package com.checkin.app.checkin.ManagerOrders;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;


public class ManagerOrdersViewModel extends BaseViewModel {
    private final ManagerOrdersRepository mRepository;
    private MediatorLiveData<Resource<ActiveSessionModel>> mBriefData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersNewData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersAcceptedData = new MediatorLiveData<>();
    private MutableLiveData<JsonNode> mErrors = new MutableLiveData<>();
    public MutableLiveData<Integer> updateOrderListSize1 = new MutableLiveData<>();
    SessionOrderedItemModel sessionOrderedItemModel = new SessionOrderedItemModel();

    public ManagerOrdersViewModel(@NonNull Application application) {
        super(application);
        mRepository = ManagerOrdersRepository.getInstance(application);
    }


    @Override
    public void updateResults() {
        fetchManagerOrdersDetails(3);
    }

    public void fetchManagerOrdersBrief(int sessionId) {
        mBriefData.addSource(mRepository.getManagerOrdersBrief(sessionId),mBriefData::setValue);
    }

    public void fetchManagerOrdersDetails(int sessionId) {
        mOrdersData.addSource(mRepository.getManagerOrdersDetails(sessionId),mOrdersData::setValue);
        newOrderList();
    }
    public LiveData<Resource<ActiveSessionModel>> getManagerOrdersBriefData() {
        return mBriefData;
    }

    public LiveData<Integer> getNewItemCount() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.data != null) {
                for (SessionOrderedItemModel item : input.data)
                    if (item.getStatus() == OPEN)
                        list.add(item);
            }
            return list.size();
        });
    }

    public LiveData<Integer> getInProgressItemCount() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.data != null) {
                for (SessionOrderedItemModel item : input.data)
                    if (item.getStatus() == IN_PROGRESS)
                        list.add(item);
            }
            return list.size();
        });
    }

    public LiveData<Integer> getDeliveredItemCount() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.data != null) {
                for (SessionOrderedItemModel item : input.data)
                    if (item.getStatus() == DONE)
                        list.add(item);
            }
            return list.size();
        });
    }


    public void newOrderList() {
        LiveData<Resource<List<SessionOrderedItemModel>>> liveDataNew = Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input == null || input.data == null)
                return Resource.loading(null);
            if(input.status == Resource.Status.SUCCESS)
            for (SessionOrderedItemModel data : input.data) {
                if (data.getStatus() == OPEN)
                    list.add(data);
            }
            return Resource.cloneResource(input, list);
        });
        mOrdersNewData.addSource(liveDataNew, mOrdersNewData::setValue);

        LiveData<Resource<List<SessionOrderedItemModel>>> liveDataAccepted = Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input == null || input.data == null)
                return Resource.loading(null);
            if(input.status == Resource.Status.SUCCESS)
            for (SessionOrderedItemModel data : input.data) {
                if (data.getStatus() != OPEN)
                    list.add(data);
            }
            return Resource.cloneResource(input, list);
        });
        mOrdersAcceptedData.addSource(liveDataAccepted, mOrdersAcceptedData::setValue);

    }

    public MediatorLiveData<Resource<List<SessionOrderedItemModel>>> getNewData() {
        return mOrdersNewData;
    }

    public MediatorLiveData<Resource<List<SessionOrderedItemModel>>> getAcceptedData() {
        return mOrdersAcceptedData;
    }

    public void sendOrderStatus(int orderId, int statusType) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("status", statusType);
        mData.addSource(mRepository.changeManagerOrderStatus(orderId,data), mData::setValue);
    }


    public void showError(JsonNode data) {
        mErrors.setValue(data);
    }

    public LiveData<JsonNode> getErrors() {
        return mErrors;
    }

}
