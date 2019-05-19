package com.checkin.app.checkin.session.activesession;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.ActiveSessionModel;
import com.checkin.app.checkin.session.model.SessionCustomerModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.session.model.TrendingDishModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mMemberUpdate = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();

    private MediatorLiveData<Resource<List<TrendingDishModel>>> mTrendingData = new MediatorLiveData<>();

    private boolean mIsRequestedCheckout = false;

    private long mShopPk = -1, mSessionPk = -1;

    public ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchActiveSessionDetail();
        fetchSessionOrders();
    }

    public void fetchActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), (resource) -> {
            if (resource != null && resource.data != null)
                mIsRequestedCheckout = resource.data.isRequestedCheckout();
            mSessionData.setValue(resource);
        });
    }

    public LiveData<Resource<ActiveSessionModel>> getSessionData() {
        return mSessionData;
    }

    public void addMembers(long id) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("user_id", id);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public void sendSelfPresence(boolean isPublic) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("is_public", isPublic);
        mData.addSource(mRepository.putSelfPresence(data), mData::setValue);
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrdersData() {
        return mOrdersData;
    }

    public void fetchSessionOrders() {
        mOrdersData.addSource(mRepository.getSessionOrdersDetails(), mOrdersData::setValue);
    }

    public void deleteSessionOrder(long orderId) {
        mData.addSource(mRepository.removeSessionOrder(orderId), mData::setValue);
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void setShopPk(long shopPk) {
        mShopPk = shopPk;
    }

    public long getSessionPk() {
        return mSessionPk;
    }

    public void setSessionPk(long sessionPk) {
        mSessionPk = sessionPk;
    }

    public void updateBill(double bill) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
            resource.data.setBill(String.valueOf(bill));
        }
        mSessionData.setValue(resource);
    }

    public void acceptSessionMember(String userId) {
        mMemberUpdate.addSource(mRepository.acceptSessionMemberRequest(userId), mMemberUpdate::setValue);
    }

    public void removeSessionMember(String userId) {
        mMemberUpdate.addSource(mRepository.deleteSessionMember(userId), mMemberUpdate::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getSessionMemberUpdate() {
        return mMemberUpdate;
    }

    public void updateHost(BriefModel user) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.setHost(user);
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void addCustomer(SessionCustomerModel customer) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.addCustomer(customer);
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void updateCustomer(long pk, boolean isAdded) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        int pos = -1;
        for (int i = 0, count = resource.data.getCustomers().size(); i < count; i++) {
            if (Long.valueOf(resource.data.getCustomers().get(i).getUser().getPk()) == pk) {
                pos = i;
                break;
            }
        }

        if (pos > -1) {
            if (isAdded) {
                resource.data.getCustomers().get(pos).setAccepted(true);
            } else {
                resource.data.getCustomers().remove(pos);
            }

        }
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
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

    public void addNewOrder(SessionOrderedItemModel sessionOrderedItem) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        for (SessionOrderedItemModel orderedItemModel : listResource.data) {
            if (orderedItemModel.getPk() == sessionOrderedItem.getPk())
                return;
        }
        listResource.data.add(0, sessionOrderedItem);
        mOrdersData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        for (SessionOrderedItemModel orderedItemModel : listResource.data) {
            if (orderedItemModel.getPk() == orderPk) {
                orderedItemModel.setStatus(sessionEventStatus.tag);
                break;
            }
        }
        mOrdersData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public boolean isRequestedCheckout() {
        return mIsRequestedCheckout;
    }

    public void setRequestedCheckout(boolean isRequestedCheckout) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.setRequestedCheckout(isRequestedCheckout);
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void fetchTrendingItem() {
        mTrendingData.addSource(mRepository.getTrendingDishes(mShopPk), mTrendingData::setValue);
    }

    public LiveData<Resource<List<TrendingDishModel>>> getMenuTrendingItems() {
        return mTrendingData;
    }
}
