package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;

import java.util.List;

public class ActiveSessionOrdersViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;

    private MediatorLiveData<Resource<List<SessionViewOrdersModel>>> mOrdersData = new MediatorLiveData<>();

    private String mShopPk;

    ActiveSessionOrdersViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getSessionOrdersData();
    }

    public LiveData<Resource<List<SessionViewOrdersModel>>> getSessionOrdersData() {
        mOrdersData.addSource(mRepository.getSessionOrdersDetails(),mOrdersData::setValue);
        return mOrdersData;
    }


    public void cancelOrders(String pk) { }

    public void checkoutSession() {
        Log.e("Session", "Checked out");
    }


    public void deleteSessionOrder(String order_id) {
        mData.addSource(mRepository.removeSessionOrder(order_id), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void setShopPk(String shopPk) {
        mShopPk = shopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }
}
