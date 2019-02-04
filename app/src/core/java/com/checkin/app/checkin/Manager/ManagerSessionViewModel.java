package com.checkin.app.checkin.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;


public class ManagerSessionViewModel extends BaseViewModel {
    private final ManagerRepository mManagerRepository;
    private final SessionRepository mSessionRepository;
    private final WaiterRepository mWaiterRepository;

    private MediatorLiveData<Resource<SessionBriefModel>> mBriefData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ManagerSessionEventModel>>> mEventData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mDetailData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<OrderStatusModel>> mOrderStatusData = new MediatorLiveData<>();

    private long mSessionPk;
    private long mShopPk;

    public ManagerSessionViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.getInstance(application);
        mSessionRepository = SessionRepository.getInstance(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchSessionOrders();
        fetchSessionEvents();
    }

    public LiveData<Resource<ManagerSessionInvoiceModel>> getSessionInvoice(long sessionId) {
        mSessionPk = sessionId;
        return mManagerRepository.getManagerSessionInvoice(sessionId);
    }

    public void approveBill(double discountPercent) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("discount_percent", discountPercent);
        mDetailData.addSource(mManagerRepository.putManagerSessionApproveCheckout(mSessionPk, data), mDetailData::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getDetailData() {
        return mDetailData;
    }

    public void fetchSessionBriefData(long sessionId) {
        mSessionPk = sessionId;
        mBriefData.addSource(mSessionRepository.getSessionBriefDetail(sessionId), mBriefData::setValue);
    }

    public void fetchSessionOrders() {
        mOrdersData.addSource(mSessionRepository.getSessionOrders(mSessionPk), mOrdersData::setValue);
    }

    public void fetchSessionEvents() {
        mEventData.addSource(mSessionRepository.getSessionEvents(mSessionPk), mEventData::setValue);
    }

    public LiveData<Resource<SessionBriefModel>> getSessionBriefData() {
        return mBriefData;
    }

    public LiveData<Resource<List<ManagerSessionEventModel>>> getSessionEventData() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.data == null)
                return input;
            List<ManagerSessionEventModel> list = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS)
                for (ManagerSessionEventModel data : input.data) {
                    if(data.getType() != EVENT_MENU_ORDER_ITEM)
                        list.add(data);
                }
            return Resource.cloneResource(input, list);
        });
    }

    public LiveData<Integer> getCountNewOrders() {
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

    public LiveData<Integer> getCountProgressOrders() {
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

    public LiveData<Integer> getCountDeliveredOrders() {
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

    public LiveData<Resource<List<SessionOrderedItemModel>>> getOpenOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.data == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.data) {
                    if (data.getStatus() == OPEN)
                        list.add(data);
                }
            return Resource.cloneResource(input, list);
        });
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getAcceptedOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.data == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.data) {
                    if (data.getStatus() != OPEN)
                        list.add(data);
                }
            return Resource.cloneResource(input, list);
        });
    }

    public void updateOrderStatus(int orderId, int statusType) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("status", statusType);
        mOrderStatusData.addSource(mWaiterRepository.changeOrderStatus(orderId, data), mOrderStatusData::setValue);
    }

    public LiveData<Resource<OrderStatusModel>> getOrderStatusData() {
        return mOrderStatusData;
    }

    public long getSessionPk() {
        return mSessionPk;
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void setShopPk(long shopId) {
        mShopPk = shopId;
    }

    public void markEventDone(long eventPk) {
        mDetailData.addSource(mWaiterRepository.markEventDone(eventPk), mDetailData::setValue);
    }
}
