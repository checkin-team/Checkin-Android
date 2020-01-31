package com.checkin.app.checkin.session.activesession;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.Converters;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.ActiveSessionModel;
import com.checkin.app.checkin.session.models.SessionCustomerModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;
import com.checkin.app.checkin.session.models.TrendingDishModel;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private SourceMappedLiveData<Resource<ActiveSessionModel>> mSessionData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<GenericDetailModel>> mMemberUpdate = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<TrendingDishModel>>> mTrendingData = createNetworkLiveData();

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
            if (resource != null && resource.getData() != null)
                mIsRequestedCheckout = resource.getData().isRequestedCheckout();
            mSessionData.setValue(resource);
        });
    }

    public LiveData<Resource<ActiveSessionModel>> getSessionData() {
        return mSessionData;
    }

    public void addMembers(long id) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("user_id", id);
        getMData().addSource(mRepository.postAddMembers(data), getMData()::setValue);
    }

    public void sendSelfPresence(boolean isPublic) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("is_public", isPublic);
        getMData().addSource(mRepository.putSelfPresence(data), getMData()::setValue);
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrdersData() {
        return mOrdersData;
    }

    public void fetchSessionOrders() {
        mOrdersData.addSource(mRepository.getSessionOrdersDetails(), mOrdersData::setValue);
    }

    public void deleteSessionOrder(long orderId) {
        getMData().addSource(mRepository.removeSessionOrder(orderId), getMData()::setValue);
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
        if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
            resource.getData().setBill(bill);
        }
        mSessionData.setValue(resource);
    }

    public void acceptSessionMember(long userId) {
        mMemberUpdate.addSource(mRepository.acceptSessionMemberRequest(userId), mMemberUpdate::setValue);
    }

    public void removeSessionMember(long userId) {
        mMemberUpdate.addSource(mRepository.deleteSessionMember(userId), mMemberUpdate::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getSessionMemberUpdate() {
        return mMemberUpdate;
    }

    public void updateHost(BriefModel user) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        resource.getData().setHost(user);
        mSessionData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void addCustomer(SessionCustomerModel customer) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        resource.getData().addCustomer(customer);
        mSessionData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void updateCustomer(long pk, boolean isAdded) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = resource.getData().getCustomers().size(); i < count; i++) {
            if (Long.valueOf(resource.getData().getCustomers().get(i).getUser().getPk()) == pk) {
                pos = i;
                break;
            }
        }

        if (pos > -1) {
            if (isAdded) {
                resource.getData().getCustomers().get(pos).setAccepted(true);
            } else {
                resource.getData().getCustomers().remove(pos);
            }

        }
        mSessionData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
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

    public void addNewOrder(SessionOrderedItemModel sessionOrderedItem) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        for (SessionOrderedItemModel orderedItemModel : listResource.getData()) {
            if (orderedItemModel.getPk() == sessionOrderedItem.getPk())
                return;
        }
        listResource.getData().add(0, sessionOrderedItem);
        mOrdersData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        Resource<List<SessionOrderedItemModel>> listResource = mOrdersData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        for (SessionOrderedItemModel orderedItemModel : listResource.getData()) {
            if (orderedItemModel.getPk() == orderPk) {
                orderedItemModel.setStatus(sessionEventStatus.tag);
                break;
            }
        }
        mOrdersData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public boolean isRequestedCheckout() {
        return mIsRequestedCheckout;
    }

    public void setRequestedCheckout(boolean isRequestedCheckout) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.getData() == null)
            return;
        resource.getData().setRequestedCheckout(isRequestedCheckout);
        mSessionData.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }

    public void fetchTrendingItem() {
        mTrendingData.addSource(mRepository.getTrendingDishes(mShopPk), mTrendingData::setValue);
    }

    public LiveData<Resource<List<TrendingDishModel>>> getMenuTrendingItems() {
        return mTrendingData;
    }
}
