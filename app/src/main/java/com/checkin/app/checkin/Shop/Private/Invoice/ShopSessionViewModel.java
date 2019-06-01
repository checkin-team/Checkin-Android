package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class ShopSessionViewModel extends BaseViewModel {
    private ShopInvoiceRepository mRepository;

    private MediatorLiveData<Resource<ShopSessionDetailModel>> mDetailData = new MediatorLiveData<>();

    private long mSessionPk;

    public ShopSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopInvoiceRepository.getInstance(application);
    }

    @Override
    protected void registerProblemHandlers() {
        mDetailData = registerProblemHandler(mDetailData);
    }

    public void fetchSessionDetail() {
        mDetailData.addSource(mRepository.getShopSessionDetail(mSessionPk), mDetailData::setValue);
    }

    public LiveData<Resource<ShopSessionDetailModel>> getSessionDetail() {
        return mDetailData;
    }

    @Override
    public void updateResults() {
    }

    public void setSessionPk(long sessionPk) {
        this.mSessionPk = sessionPk;
    }
}
