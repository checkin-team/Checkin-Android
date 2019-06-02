package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;

public class ShopSessionViewModel extends BaseViewModel {
    private ShopInvoiceRepository mRepository;

    private SourceMappedLiveData<Resource<ShopSessionDetailModel>> mDetailData = createNetworkLiveData();

    private long mSessionPk;

    public ShopSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopInvoiceRepository.getInstance(application);
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
