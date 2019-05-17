package com.checkin.app.checkin.session.activesession;

import android.app.Application;
import android.os.Bundle;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.checkin.app.checkin.session.paytm.PaytmModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.ActiveSessionModel;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.SessionCustomerModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

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
    private MediatorLiveData<Resource<List<SessionPromoModel>>> mPromoCodes = new MediatorLiveData<>();

    private long mShopPk = -1, mSessionPk = -1;
    private String paytmChecksum, paytmCustId;

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
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
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

    public void requestPaytmDetails(){
        mPaytmData.addSource(mRepository.postPaytmDetailRequest(), mPaytmData::setValue);
    }

    public LiveData<Resource<PaytmModel>> getPaytmDetails() {
        return mPaytmData;
    }

    public void setChecksumCustId(String checksum, String custId){
        paytmChecksum = checksum;
        paytmCustId = custId;
    }

    public void postPaytmCallback(Bundle bundle) {
        ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("MID", bundle.getString("MID"))
                .put("ORDERID", bundle.getString("ORDERID"))
                .put("CUST_ID", paytmCustId)
                .put("CHECKSUMHASH", paytmChecksum)
                .put("TXNAMOUNT", bundle.getString("TXNAMOUNT"))
                .put("TXNID", bundle.getString("RESPCODE"))
                .put("RESPCODE", bundle.getString("RESPCODE"))
                .put("STATUS", bundle.getString("STATUS"))
                .put("RESPMSG", bundle.getString("RESPMSG"))
                .put("channel_id", "WAP");
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
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return false;
        return resource.data.isRequestedCheckout();
    }

    public void setRequestedCheckout(boolean isRequestedCheckout) {
        Resource<ActiveSessionModel> resource = mSessionData.getValue();
        if (resource == null || resource.data == null)
            return;
        resource.data.setRequestedCheckout(isRequestedCheckout);
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
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


}
