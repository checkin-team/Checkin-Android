package com.checkin.app.checkin.Waiter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.Converters;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.session.SessionRepository;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.session.models.CheckoutStatusModel;
import com.checkin.app.checkin.session.models.SessionBriefModel;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class WaiterTableViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;
    private SessionRepository mSessionRepository;

    private SourceMappedLiveData<Resource<SessionBriefModel>> mSessionDetail = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<WaiterEventModel>>> mEventData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<GenericDetailModel>> mEventUpdate = createNetworkLiveData();
    private SourceMappedLiveData<Resource<OrderStatusModel>> mOrderStatus = createNetworkLiveData();
    private SourceMappedLiveData<Resource<CheckoutStatusModel>> mCheckoutData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<SessionContactModel>>> mContactListData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<OrderStatusModel>>> mResultOrderStatus = createNetworkLiveData();

    private MutableLiveData<List<OrderStatusModel>> mNewOrderStatus = new MutableLiveData<>();

    private long mSessionPk;

    public WaiterTableViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.Companion.getInstance(application);
        mSessionRepository = SessionRepository.Companion.getInstance(application);
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
            if (input == null || input.getData() == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();

            if (input.getStatus() == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel : input.getData()) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.OPEN || eventModel.getStatus() == CHAT_STATUS_TYPE.IN_PROGRESS || eventModel.getStatus() == CHAT_STATUS_TYPE.COOKED)
                        result.add(eventModel);
                }
                return Resource.Companion.cloneResource(input, result);
            }
            return input;
        });
    }

    public void postSessionContact(String email, String phone) {
        getMData().addSource(mWaiterRepository.postSessionContact(mSessionPk, new SessionContactModel(phone, email)), getMData()::setValue);
    }

    public void fetchSessionContacts() {
        mContactListData.addSource(mWaiterRepository.getSessionContacts(mSessionPk), mContactListData::setValue);
    }

    public LiveData<Resource<List<SessionContactModel>>> getSessionContactListData() {
        return mContactListData;
    }

    public LiveData<Resource<List<WaiterEventModel>>> getDeliveredTableEvents() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.getData() == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();
            if (input.getStatus() == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel : input.getData()) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.DONE)
                        result.add(eventModel);
                }
                return Resource.Companion.cloneResource(input, result);
            }
            return input;
        });
    }

    public void requestSessionCheckout() {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
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
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
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
        mResultOrderStatus.addSource(mWaiterRepository.postOrderListStatus(mSessionPk, mNewOrderStatus.getValue()), mResultOrderStatus::setValue);
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
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            if (listResource.getData().get(i).getPk() == eventId) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel eventModel = listResource.getData().get(pos);
            listResource.getData().remove(pos);
            eventModel.setStatus(CHAT_STATUS_TYPE.DONE);
            listResource.getData().add(0, eventModel);
        }
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateOrderItemStatus(long eventId, CHAT_STATUS_TYPE status) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            if (listResource.getData().get(i).getPk() == eventId) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel event = listResource.getData().get(pos);
            listResource.getData().remove(pos);
            event.setStatus(status);
            if (event.getOrderedItem() != null)
                event.getOrderedItem().setStatus(status.tag);
            listResource.getData().add(0, event);
        }
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateUiMarkOrderStatus(OrderStatusModel data) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            WaiterEventModel eventModel = listResource.getData().get(i);
            if (eventModel.getOrderedItem() != null && eventModel.getOrderedItem().getPk() == data.getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel event = listResource.getData().get(pos);
            listResource.getData().remove(pos);
            event.setStatus(data.getStatus());
            event.getOrderedItem().setStatus(data.getStatus());
            listResource.getData().add(0, event);
        }
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateUiOrderListStatus(List<OrderStatusModel> data) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;

        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            WaiterEventModel eventModel = listResource.getData().get(i);
            if (eventModel.getOrderedItem() != null && eventModel.getOrderedItem().getPk() == data.get(0).getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            WaiterEventModel event = listResource.getData().get(pos);
            listResource.getData().remove(pos);
            event.setStatus(data.get(0).getStatus());
            event.getOrderedItem().setStatus(data.get(0).getStatus());
            listResource.getData().add(0, event);
        }
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void initiateCollectCash(double bill, ShopModel.PAYMENT_MODE sessionBillPaymentMode) {
        Resource<SessionBriefModel> resource = mSessionDetail.getValue();
        if (resource == null || resource.getData() == null)
            return;
        SessionBriefModel data = resource.getData();
        data.setBill(bill);
        data.setPaymentModes(sessionBillPaymentMode.tag);
        data.setRequestedCheckout(true);
        mSessionDetail.setValue(Resource.Companion.cloneResource(resource, data));
    }

    public void addNewEvent(WaiterEventModel waiterEventModel) {
        Resource<List<WaiterEventModel>> listResource = mEventData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        for (WaiterEventModel event : listResource.getData()) {
            if (event.getPk() == waiterEventModel.getPk())
                return;
        }
        listResource.getData().add(0, waiterEventModel);
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateMemberCount(int customerCount) {
        Resource<SessionBriefModel> resource = mSessionDetail.getValue();
        if (resource == null || resource.getData() == null)
            return;
        resource.getData().setCustomerCount(customerCount);
        mSessionDetail.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }
}
