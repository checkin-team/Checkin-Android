package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;

import java.util.List;

public class ActiveSessionOrdersViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<List<SessionOrderedItemModel>>> mOrdersData = new MediatorLiveData<>();

    private String mShopPk;

    public ActiveSessionOrdersViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getSessionOrdersData();
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrdersData() {
        mOrdersData.addSource(mRepository.getSessionOrdersDetails(),mOrdersData::setValue);
        return mOrdersData;
    }


    public void cancelOrders(String pk) { }

    public void checkoutSession() {
        Log.e("Session", "Checked out");
    }


    public void deleteSessionOrder(long orderId) {
        mData.addSource(mRepository.removeSessionOrder(orderId), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void setShopPk(String shopPk) {
        mShopPk = shopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }
}
