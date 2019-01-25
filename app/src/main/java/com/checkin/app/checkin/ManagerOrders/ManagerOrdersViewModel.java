package com.checkin.app.checkin.ManagerOrders;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;


public class ManagerOrdersViewModel extends BaseViewModel {
    private final ManagerOrdersRepository mRepository;
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersNewData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersAcceptedData = new MediatorLiveData<>();

    public ManagerOrdersViewModel(@NonNull Application application) {
        super(application);
        mRepository = ManagerOrdersRepository.getInstance(application);
    }


    @Override
    public void updateResults() {
        fetchManagerOrdersDetails(1);
    }

    public void fetchManagerOrdersDetails(int sessionId) {
        mOrdersData.addSource(mRepository.getManagerOrdersDetails(sessionId),mOrdersData::setValue);
        newOrderList();
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



}
