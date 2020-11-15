package com.checkin.app.checkin.cook.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Waiter.WaiterRepository;
import com.checkin.app.checkin.Waiter.models.OrderStatusModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.Converters;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.session.SessionRepository;
import com.checkin.app.checkin.session.models.SessionBriefModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.CANCELLED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.COOKED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;


public class CookSessionViewModel extends BaseViewModel {
    private final SessionRepository mSessionRepository;
    private final WaiterRepository mWaiterRepository;

    private final SourceMappedLiveData<Resource<SessionBriefModel>> mBriefData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<OrderStatusModel>> mOrderStatusData = createNetworkLiveData();
    private final SourceMappedLiveData<Resource<List<OrderStatusModel>>> mResultOrderStatus = createNetworkLiveData();

    private final MutableLiveData<List<OrderStatusModel>> mNewOrderStatus = new MutableLiveData<>();

    private long mSessionPk;
    private long mShopPk;

    public CookSessionViewModel(@NonNull Application application) {
        super(application);
        mSessionRepository = SessionRepository.Companion.getInstance(application);
        mWaiterRepository = WaiterRepository.Companion.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchSessionOrders();
        fetchSessionBriefData(mSessionPk);
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

    public LiveData<Resource<SessionBriefModel>> getSessionBriefData() {
        return mBriefData;
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
                    if (data.getStatus() == IN_PROGRESS)
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
                    if (data.getStatus() == DONE || data.getStatus() == CANCELLED || data.getStatus() == COOKED)
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
        mNewOrderStatus.setValue(null);
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

    public long getSessionPk() {
        return mSessionPk;
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void setShopPk(long shopId) {
        mShopPk = shopId;
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
}
