package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.Model.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class ActiveSessionViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<SessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mMemberUpdate = new MediatorLiveData<>();

    private long mShopPk = -1, mSessionPk = -1;

    public ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchActiveSessionDetail();
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

    public LiveData<Resource<SessionInvoiceModel>> getSessionInvoice() {
        return mInvoiceData;
    }

    public void fetchSessionInvoice() {
        mInvoiceData.addSource(mRepository.getActiveSessionInvoice(), mInvoiceData::setValue);
    }

    public void requestCheckout(double tip, ShopModel.PAYMENT_MODE paymentMode) {
        ObjectNode data = Converters.objectMapper.createObjectNode()
                .put("tip", tip)
                .put("payment_mode", paymentMode.tag);
        mData.addSource(mRepository.postRequestCheckout(data), mData::setValue);
    }

    public void setShopPk(long shopPk) {
        mShopPk = shopPk;
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void setSessionPk(long sessionPk) {
        mSessionPk = sessionPk;
    }

    public long getSessionPk() {
        return mSessionPk;
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
            if (isAdded){
                resource.data.getCustomers().get(pos).setAccepted(true);
            }else {
                resource.data.getCustomers().remove(pos);
            }

        }
        mSessionData.setValue(Resource.cloneResource(resource, resource.data));
    }
}
