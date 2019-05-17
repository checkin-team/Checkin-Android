package com.checkin.app.checkin.session.activesession;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.paytm.PaytmModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.ActiveSessionModel;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.SessionCustomerModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.checkin.app.checkin.session.model.TrendingDishModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.DONE;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mMemberUpdate = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<CheckoutStatusModel>> mCheckoutData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<PaytmModel>> mPaytmData = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mIsRequestedCheckout = new MutableLiveData<>(false);
    private MediatorLiveData<Resource<List<TrendingDishModel>>> mTrendingData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<SessionPromoModel>>> mPromoCodes = new MediatorLiveData<>();

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
                mIsRequestedCheckout.setValue(resource.data.isRequestedCheckout());
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

    public LiveData<Resource<SessionInvoiceModel>> getSessionInvoice() {
        return mInvoiceData;
    }

    public void fetchSessionInvoice() {
        mInvoiceData.addSource(mRepository.getActiveSessionInvoice(), mInvoiceData::setValue);
    }

    public void requestCheckout(double tip, ShopModel.PAYMENT_MODE paymentMode, boolean override) {
        ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("tip", tip)
                .put("payment_mode", paymentMode.tag)
                .put("override", override);
        mCheckoutData.addSource(mRepository.postRequestCheckout(data), mCheckoutData::setValue);
    }

    public void requestPaytmDetails() {
        mPaytmData.addSource(mRepository.postPaytmDetailRequest(), mPaytmData::setValue);
    }

    public LiveData<Resource<PaytmModel>> getPaytmDetails() {
        return mPaytmData;
    }

    public void postPaytmCallback(Bundle bundle) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
                data.put(key, String.valueOf(bundle.get(key)));
        }
        mData.addSource(mRepository.postPaytmResult(data), mData::setValue);
    }

    public LiveData<Resource<CheckoutStatusModel>> getCheckoutData() {
        return mCheckoutData;
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
        Boolean result = mIsRequestedCheckout.getValue();
        return result != null ? result : false;
    }

    public LiveData<Boolean> getRequestedCheckout() {
        return mIsRequestedCheckout;
    }

    public void updateRequestCheckout(boolean isCheckout) {
        mIsRequestedCheckout.setValue(isCheckout);
    }

    public void setRequestedCheckout(boolean isRequestedCheckout) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.setRequestedCheckout(isRequestedCheckout);
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
    }

    public void fetchTrendingItem(){
        mTrendingData.addSource(mRepository.getTrendingDishes(mShopPk), mTrendingData::setValue);
    }

    public LiveData<Resource<List<TrendingDishModel>>> getTrendingItem() {
        return mTrendingData;
    }

    public void fetchAvailablePromoCodes() {
        mPromoCodes.addSource(mRepository.getAvailablePromoCodes(), mPromoCodes::setValue);
    }

    public LiveData<Resource<List<SessionPromoModel>>> getPromoCodes() {
        return mPromoCodes;
    }

    public void availPromoCode(String code) {
        ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("code", code);
        mData.addSource(mRepository.postAvailPromoCode(data), mData::setValue);
    }

    public void searchPromoCodes(final String query) {
        if (query == null || query.isEmpty())
            return;
        mPromoCodes.setValue(Resource.loading(null));
            LiveData<Resource<List<SessionPromoModel>>> resourceLiveData = Transformations.map(mPromoCodes, input -> {
                if (input == null || input.data == null)
                    return Resource.loading(null);
                List<SessionPromoModel> items = new ArrayList<>();
                List<SessionPromoModel> data = input.data;
                for (int i =0; i<data.size(); i++) {
                        if (data.get(i).getCode().equalsIgnoreCase(query))
                            items.add(data.get(i));

                }
                if (items.size() == 0)
                    return Resource.errorNotFound(null);
                return Resource.cloneResource(input, items);
            });
            mPromoCodes.addSource(resourceLiveData, mPromoCodes::setValue);
    }

    public void resetPromoItems() {
        mPromoCodes.setValue(Resource.noRequest());
    }


}
