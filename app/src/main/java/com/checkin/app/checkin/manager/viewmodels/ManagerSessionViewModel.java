package com.checkin.app.checkin.manager.viewmodels;

import android.app.Application;

import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.checkin.app.checkin.Waiter.models.OrderStatusModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.Converters;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.manager.ManagerRepository;
import com.checkin.app.checkin.manager.models.GuestContactModel;
import com.checkin.app.checkin.manager.models.ManagerSessionEventModel;
import com.checkin.app.checkin.manager.models.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.session.SessionRepository;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.CheckoutStatusModel;
import com.checkin.app.checkin.session.models.SessionBriefModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.CANCELLED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.COOKED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;


public class ManagerSessionViewModel extends BaseViewModel {
    private final ManagerRepository mManagerRepository;
    private final SessionRepository mSessionRepository;
    private final WaiterRepository mWaiterRepository;

    private final SourceMappedLiveData<Resource<SessionBriefModel>> mBriefData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<ManagerSessionEventModel>>> mEventData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<GenericDetailModel>> mDetailData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<OrderStatusModel>> mOrderStatusData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<CheckoutStatusModel>> mCheckoutData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<GuestContactModel>>> mContactListData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<OrderStatusModel>>> mResultOrderStatus = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<ManagerSessionInvoiceModel>> mInvoiceData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<ObjectNode>> mSwitchTableData = createNetworkLiveData();

    private final MutableLiveData<List<OrderStatusModel>> mNewOrderStatus = new MutableLiveData<>();

    private boolean discountInINR;
    private long mSessionPk;
    private long mShopPk;
    private PAYMENT_MODE mPaymentMode = PAYMENT_MODE.CASH;
    private boolean mIsRequestedCheckout;
    private boolean mIsPromoApplied;

    public ManagerSessionViewModel(@NonNull Application application) {
        super(application);
        mManagerRepository = ManagerRepository.Companion.getInstance(application);
        mSessionRepository = SessionRepository.Companion.getInstance(application);
        mWaiterRepository = WaiterRepository.Companion.getInstance(application);
    }

    public void putSessionCheckout() {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("payment_mode", mPaymentMode.tag);
        mCheckoutData.addSource(mManagerRepository.manageSessionCheckout(mSessionPk, data), mCheckoutData::setValue);
    }

    public LiveData<Resource<CheckoutStatusModel>> getCheckoutData() {
        return mCheckoutData;
    }

    @Override
    public void updateResults() {
        fetchSessionOrders();
        fetchSessionEvents();
        fetchSessionBriefData(mSessionPk);
    }

    public void fetchManagerInvoice(long sessionId) {
        mSessionPk = sessionId;
        mInvoiceData.addSource(mManagerRepository.getManagerSessionInvoice(sessionId), mInvoiceData::setValue);
    }

    public LiveData<Resource<ManagerSessionInvoiceModel>> getSessionInvoice() {
        return mInvoiceData;
    }

    public boolean isDiscountInINR() {
        return discountInINR;
    }

    public void setDiscountInINR(boolean isINR) {
        discountInINR = isINR;
    }

    public void updateDiscount(double value) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        if (discountInINR)
            data.put("discount_amount", value);
        else
            data.put("discount_percent", value);

        mDetailData.addSource(mManagerRepository.putManageSessionBill(mSessionPk, data), mDetailData::setValue);
    }

    public void requestSessionCheckout() {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("payment_mode", mPaymentMode.tag);
        mCheckoutData.addSource(mWaiterRepository.postSessionRequestCheckout(mSessionPk, data), mCheckoutData::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getDetailData() {
        return mDetailData;
    }

    public void fetchSessionBriefData(long sessionId) {
        mSessionPk = sessionId;
        mBriefData.addSource(mSessionRepository.getSessionBriefDetail(sessionId), mBriefData::setValue);
    }

    public void fetchSessionBriefData() {
        fetchSessionBriefData(mSessionPk);
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
            if (input == null || input.getData() == null)
                return input;
            List<ManagerSessionEventModel> list = new ArrayList<>();
            if (input.getStatus() == Resource.Status.SUCCESS)
                for (ManagerSessionEventModel data : input.getData()) {
                    if (data.getType() != EVENT_MENU_ORDER_ITEM)
                        list.add(data);
                }
            return Resource.Companion.cloneResource(input, list);
        });
    }

    public LiveData<Integer> getCountNewOrders() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.getData() != null) {
                for (SessionOrderedItemModel item : input.getData())
                    if (item.getStatus() == OPEN)
                        list.add(item);
            }
            return list.size();
        });
    }

    public LiveData<Integer> getCountProgressOrders() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.getData() != null) {
                for (SessionOrderedItemModel item : input.getData())
                    if (item.getStatus() == IN_PROGRESS)
                        list.add(item);
            }
            return list.size();
        });
    }

    public LiveData<Integer> getCountDeliveredOrders() {
        return Transformations.map(mOrdersData, input -> {
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.getData() != null) {
                for (SessionOrderedItemModel item : input.getData())
                    if (item.getStatus() == DONE)
                        list.add(item);
            }
            return list.size();
        });
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getOpenOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.getData() == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            List<OrderStatusModel> listNewOrderStatus = new ArrayList<>();

            if (input.getStatus() == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.getData()) {
                    if (data.getStatus() == OPEN) {
                        list.add(data);
                        listNewOrderStatus.add(new OrderStatusModel(data.getPk(), IN_PROGRESS));
                    }
                }
            mNewOrderStatus.setValue(listNewOrderStatus);
            return Resource.Companion.cloneResource(input, list);
        });
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getAcceptedOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.getData() == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.getStatus() == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.getData()) {
                    if (data.getStatus() == IN_PROGRESS || data.getStatus() == COOKED)
                        list.add(data);
                }
            return Resource.Companion.cloneResource(input, list);
        });
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getDeliveredRejectedOrders() {
        return Transformations.map(mOrdersData, input -> {
            if (input == null || input.getData() == null)
                return input;
            List<SessionOrderedItemModel> list = new ArrayList<>();
            if (input.getStatus() == Resource.Status.SUCCESS)
                for (SessionOrderedItemModel data : input.getData()) {
                    if (data.getStatus() == DONE || data.getStatus() == CANCELLED)
                        list.add(data);
                }
            return Resource.Companion.cloneResource(input, list);
        });
    }

    public void updateOrderStatus(int orderId, int statusType) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("status", statusType);
        mOrderStatusData.addSource(mWaiterRepository.changeOrderStatus(orderId, data), mOrderStatusData::setValue);
    }

    public void updateOrderStatusNew(int orderId, int statusType) {
        List<OrderStatusModel> listNewOrderStatus = mNewOrderStatus.getValue();
        if (listNewOrderStatus == null)
            return;
        for (OrderStatusModel item : listNewOrderStatus) {
            if (item.getPk() == orderId) {
                item.setStatus(statusType);
                break;
            }
        }
        mNewOrderStatus.setValue(listNewOrderStatus);
    }

    public void confirmOrderStatus() {
        mResultOrderStatus.addSource(mWaiterRepository.postOrderListStatus(mSessionPk, mNewOrderStatus.getValue()), mResultOrderStatus::setValue);
    }

    public LiveData<Resource<List<OrderStatusModel>>> getOrderListStatusData() {
        return mResultOrderStatus;
    }

    public LiveData<Resource<OrderStatusModel>> getOrderStatusData() {
        return mOrderStatusData;
    }

    public void updateUiOrderStatus(OrderStatusModel data) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            if (listResource.getData().get(i).getPk() == data.getPk()) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            SessionOrderedItemModel eventModel = listResource.getData().get(pos);
            eventModel.setStatus(data.getStatus());
            listResource.getData().remove(pos);
            listResource.getData().add(0, eventModel);
        }
        mOrdersData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateUiOrderListStatus(List<OrderStatusModel> data) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;

        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            for (int j = 0, size = data.size(); j < size; j++) {
                if (listResource.getData().get(i).getPk() == data.get(j).getPk()) {
                    SessionOrderedItemModel eventModel = listResource.getData().get(i);
                    eventModel.setStatus(data.get(j).getStatus());
                    listResource.getData().remove(i);
                    listResource.getData().add(j, eventModel);
                }
            }
        }
        mOrdersData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateUiEventStatus(long eventId, SessionChatModel.CHAT_STATUS_TYPE status) {
        Resource<List<ManagerSessionEventModel>> listResource = mEventData.getValue();
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
            ManagerSessionEventModel eventModel = listResource.getData().get(pos);
            eventModel.setStatus(status);
            listResource.getData().remove(pos);
            listResource.getData().add(0, eventModel);
        }
        mEventData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
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

    public void setPaymentMode(PAYMENT_MODE mode) {
        mPaymentMode = mode;
    }

    public void markEventDone(long eventPk) {
        mDetailData.addSource(mWaiterRepository.markEventDone(eventPk), mDetailData::setValue);
    }

    public void addOrderData(SessionOrderedItemModel orderedItemModel) {
        Resource<List<SessionOrderedItemModel>> resource = mOrdersData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        for (SessionOrderedItemModel iterOrder : resource.getData()) {
            if (iterOrder.getPk() == orderedItemModel.getPk()) return;
        }
        resource.getData().add(0, orderedItemModel);
        mOrdersData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void addEventData(ManagerSessionEventModel eventModel) {
        Resource<List<ManagerSessionEventModel>> resource = mEventData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        for (ManagerSessionEventModel iterEvent : resource.getData()) {
            if (iterEvent.getPk() == eventModel.getPk()) return;
        }
        resource.getData().add(0, eventModel);
        mEventData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    @Nullable
    public SessionBriefModel getSessionData() {
        Resource<SessionBriefModel> resource = mBriefData.getValue();
        if (resource == null || resource.getData() == null)
            return null;
        return resource.getData();
    }

    public void updateSessionData(SessionBriefModel data) {
        mBriefData.setValue(Resource.Companion.success(data));
    }

    public void postSessionContact(String email, String phone) {
        GuestContactModel sessionContactModel = new GuestContactModel(phone, "", email);
        getMData().addSource(mWaiterRepository.postSessionContact(mSessionPk, sessionContactModel), getMData()::setValue);
    }

    public void fetchSessionContacts() {
        mContactListData.addSource(mWaiterRepository.getSessionContacts(mSessionPk), mContactListData::setValue);
    }

    public LiveData<Resource<List<GuestContactModel>>> getSessionContactListData() {
        return mContactListData;
    }

    public void updateOrderStatusCancel(long orderId, int statusType) {
        List<OrderStatusModel> listNewOrderStatus = new ArrayList<>();
        OrderStatusModel item = new OrderStatusModel();
        item.setPk(orderId);
        item.setStatus(statusType);
        listNewOrderStatus.add(item);
        mNewOrderStatus.setValue(listNewOrderStatus);
    }

    public void confirmCancelOrderManager() {
        mResultOrderStatus.addSource(mWaiterRepository.postOrderListStatus(mSessionPk, mNewOrderStatus.getValue()), mResultOrderStatus::setValue);
    }

    public void switchTable(long qrPk) {
        ObjectNode requestJson = Converters.INSTANCE.getObjectMapper().createObjectNode();
        requestJson.put("qr", qrPk);
        mSwitchTableData.addSource(mManagerRepository.managerSessionSwitchTable(mSessionPk, requestJson), mSwitchTableData::setValue);
    }

    public LiveData<Resource<ObjectNode>> getSessionSwitchTable() {
        return mSwitchTableData;
    }

    public boolean getIsRequestedCheckout() {
        return mIsRequestedCheckout;
    }

    public void setIsRequestedCheckout(boolean mIsRequestedCheckout) {
        this.mIsRequestedCheckout = mIsRequestedCheckout;
    }

    public boolean getIsPromoApplied() {
        return mIsPromoApplied;
    }

    public void setIsPromoApplied(boolean mIsPromoApplied) {
        this.mIsPromoApplied = mIsPromoApplied;
    }

    public enum PAYMENT_MODE {
        CASH("csh"),
        CARD("crd"),
        UPI("upi");

        public String tag;

        PAYMENT_MODE(String tag) {
            this.tag = tag;
        }
    }
}
