package com.checkin.app.checkin.Manager;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.CANCELLED;
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
    private MediatorLiveData<Resource<GenericDetailModel>> mSessionCheckoutData = new MediatorLiveData<>();

    private long mSessionPk;
    private long mShopPk;

    public ManagerSessionViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.getInstance(application);
        mSessionRepository = SessionRepository.getInstance(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
    }

    public void putSessionCheckout(long sessionId, String paymentMode){
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("payment_mode", paymentMode);
        mSessionCheckoutData.addSource(mManagerRepository.manageSessionCheckout(sessionId,data),mSessionCheckoutData::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> putSessionCheckoutData(){
        return mSessionCheckoutData;
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

    public void updateDiscount(double discountPercent) {
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
                    if (data.getType() != EVENT_MENU_ORDER_ITEM)
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
                    if (data.getStatus() == IN_PROGRESS)
                        list.add(data);
                }
            return Resource.cloneResource(input, list);
        });
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getDeliveredRejectedOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.data == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.data) {
                    if (data.getStatus() == DONE || data.getStatus() == CANCELLED)
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


    public void updateUiOrderStatus(OrderStatusModel data) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.data.size(); i < count; i++) {
            if (listResource.data.get(i).getPk() == data.getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            SessionOrderedItemModel eventModel = listResource.data.get(pos);
            eventModel.setStatus(data.getStatus().tag);
            listResource.data.remove(pos);
            listResource.data.add(0, eventModel);
        }
        mOrdersData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateUiEventStatus(long eventId, SessionChatModel.CHAT_STATUS_TYPE status) {
        Resource<List<ManagerSessionEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.data.size(); i < count; i++) {
            if (listResource.data.get(i).getPk() == eventId) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            ManagerSessionEventModel eventModel = listResource.data.get(pos);
            eventModel.setStatus(status);
            listResource.data.remove(pos);
            listResource.data.add(0, eventModel);
        }
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
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

    public void addOrderData(SessionOrderedItemModel orderedItemModel) {
        Resource<List<SessionOrderedItemModel>> resource = mOrdersData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.add(0, orderedItemModel);
        mOrdersData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void addEventData(ManagerSessionEventModel eventModel) {
        Resource<List<ManagerSessionEventModel>> resource = mEventData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.add(0, eventModel);
        mEventData.setValue(Resource.cloneResource(resource, resource.data));
    }

    @Nullable
    public SessionBriefModel getSessionData() {
        Resource<SessionBriefModel> resource = mBriefData.getValue();
        if (resource == null || resource.data == null)
            return null;
        return resource.data;
    }

    public void updateSessionData(SessionBriefModel data) {
        mBriefData.setValue(Resource.success(data));
    }
}
