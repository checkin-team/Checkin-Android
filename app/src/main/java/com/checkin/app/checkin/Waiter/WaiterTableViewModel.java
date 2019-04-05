package com.checkin.app.checkin.Waiter;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.Session.Model.CheckoutStatusModel;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class WaiterTableViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;
    private SessionRepository mSessionRepository;

    private MediatorLiveData<Resource<SessionBriefModel>> mSessionDetail = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<WaiterEventModel>>> mEventData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mEventUpdate = new MediatorLiveData<>();
    private MediatorLiveData<Resource<OrderStatusModel>> mOrderStatus = new MediatorLiveData<>();
    private MediatorLiveData<Resource<CheckoutStatusModel>> mCheckoutData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionContactModel>>> mContactListData = new MediatorLiveData<>();

    private MutableLiveData<List<OrderStatusModel>> mNewOrderStatus = new MutableLiveData<>();
    private MediatorLiveData<Resource<List<OrderStatusModel>>> mResultOrderStatus = new MediatorLiveData<>();

    private long mSessionPk;

    public WaiterTableViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
        mSessionRepository = SessionRepository.getInstance(application);
    }

    public void fetchSessionDetail(long sessionId) {
        mSessionPk = sessionId;
        mSessionDetail.addSource(mSessionRepository.getSessionBriefDetail(sessionId), mSessionDetail::setValue);
    }

    public void fetchTableEvents() {
        mEventData.addSource(mWaiterRepository.getWaiterEventsForTable(mSessionPk), mEventData::setValue);
    }

    public LiveData<Resource<SessionBriefModel>> getSessionDetail() {
        return mSessionDetail;
    }

    public LiveData<Resource<List<WaiterEventModel>>> getActiveTableEvents() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.data == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();
            List<OrderStatusModel> listNewOrderStatus = new ArrayList<>();

            if (input.status == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel : input.data) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.OPEN || eventModel.getStatus() == CHAT_STATUS_TYPE.IN_PROGRESS)
                        result.add(eventModel);

//                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.OPEN)
//                        listNewOrderStatus.add(new OrderStatusModel(eventModel.getPk(), IN_PROGRESS));
                }
//                mNewOrderStatus.setValue(listNewOrderStatus);
                return Resource.cloneResource(input, result);
            }
            return input;
        });
    }

    public void postSessionContact(String email, String phone) {
        mData.addSource(mWaiterRepository.postSessionContact(mSessionPk, new SessionContactModel(phone, email)), mData::setValue);
    }

    public void fetchSessionContacts() {
        mContactListData.addSource(mWaiterRepository.getSessionContacts(mSessionPk), mContactListData::setValue);
    }

    public LiveData<Resource<List<SessionContactModel>>> getSessionContactListData() {
        return mContactListData;
    }

    public LiveData<Resource<List<WaiterEventModel>>> getDeliveredTableEvents() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.data == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel : input.data) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.DONE)
                        result.add(eventModel);
                }
                return Resource.cloneResource(input, result);
            }
            return input;
        });
    }

    public void requestSessionCheckout() {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("payment_mode", "csh");
        mCheckoutData.addSource(mWaiterRepository.postSessionRequestCheckout(mSessionPk, data), mCheckoutData::setValue);
    }

    public LiveData<Resource<CheckoutStatusModel>> getCheckoutData() {
        return mCheckoutData;
    }

    public long getSessionPk() {
        return mSessionPk;
    }

    public void updateOrderStatus(long orderId, CHAT_STATUS_TYPE statusType) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("status", statusType.tag);
        mOrderStatus.addSource(mWaiterRepository.changeOrderStatus(orderId, data), mOrderStatus::setValue);
    }

    public LiveData<Resource<OrderStatusModel>> getOrderStatus() {
        return mOrderStatus;
    }

    public void updateOrderStatusNew(int orderId, int statusType) {
        List<OrderStatusModel> listNewOrderStatus = new ArrayList<>();
        OrderStatusModel item = new OrderStatusModel();
        item.setPk(orderId);
        item.setStatus(statusType);
        listNewOrderStatus.add(item);
        mNewOrderStatus.setValue(listNewOrderStatus);
    }

    public void confirmOrderStatusWaiter() {
        mResultOrderStatus.addSource(mWaiterRepository.postOrderListStatus(mNewOrderStatus.getValue()), mResultOrderStatus::setValue);
    }

    public LiveData<Resource<List<OrderStatusModel>>> getOrderListStatusData() {
        return mResultOrderStatus;
    }

    public void markEventDone(long eventId) {
        mEventUpdate.addSource(mWaiterRepository.markEventDone(eventId), mEventUpdate::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getEventUpdate() {
        return mEventUpdate;
    }

    @Override
    public void updateResults() {
        fetchSessionDetail(mSessionPk);
        fetchTableEvents();
    }

    public void updateUiMarkEventDone(long eventId) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
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
            WaiterEventModel eventModel = listResource.data.get(pos);
            listResource.data.remove(pos);
            eventModel.setStatus(CHAT_STATUS_TYPE.DONE);
            listResource.data.add(0, eventModel);
        }
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateOrderItemStatus(long eventId, CHAT_STATUS_TYPE status) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
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
            WaiterEventModel event = listResource.data.get(pos);
            listResource.data.remove(pos);
            event.setStatus(status);
            if (event.getOrderedItem() != null)
                event.getOrderedItem().setStatus(status.tag);
            listResource.data.add(0, event);
        }
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateUiMarkOrderStatus(OrderStatusModel data) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.data.size(); i < count; i++) {
            WaiterEventModel eventModel = listResource.data.get(i);
            if (eventModel.getOrderedItem() != null && eventModel.getOrderedItem().getPk() == data.getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel event = listResource.data.get(pos);
            listResource.data.remove(pos);
            event.setStatus(data.getStatus());
            event.getOrderedItem().setStatus(data.getStatus());
            listResource.data.add(0, event);
        }
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateUiOrderListStatus(List<OrderStatusModel> data) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.data == null)
            return;

        int pos = -1;
        for (int i = 0, count = listResource.data.size(); i < count; i++) {
            WaiterEventModel eventModel = listResource.data.get(i);
            if (eventModel.getOrderedItem() != null && eventModel.getOrderedItem().getPk() == data.get(0).getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel event = listResource.data.get(pos);
            listResource.data.remove(pos);
            event.setStatus(data.get(0).getStatus());
            event.getOrderedItem().setStatus(data.get(0).getStatus());
            listResource.data.add(0, event);
        }
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void initiateCollectCash(double bill) {
        Resource<SessionBriefModel> resource = mSessionDetail.getValue();
        if (resource == null || resource.data == null)
            return;
        SessionBriefModel data = resource.data;
        data.setBill(bill);
        data.setRequestedCheckout(true);
        mSessionDetail.setValue(Resource.cloneResource(resource, data));
    }

    public void addNewEvent(WaiterEventModel waiterEventModel) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        for (WaiterEventModel event : listResource.data) {
            if (event.getPk() == waiterEventModel.getPk())
                return;
        }
        listResource.data.add(0, waiterEventModel);
        mEventData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateMemberCount(int customerCount) {
        Resource<SessionBriefModel> resource = mSessionDetail.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.setCustomerCount(customerCount);
        mSessionDetail.setValue(Resource.cloneResource(resource, resource.data));
    }
}
