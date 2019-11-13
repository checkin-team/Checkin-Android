package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;

import java.util.List;

public class ShopSessionViewModel extends BaseViewModel {
    private ShopInvoiceRepository mRepository;

    private SourceMappedLiveData<Resource<ShopSessionDetailModel>> mDetailData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<ShopSessionFeedbackModel>>> mShopReviewData = createNetworkLiveData();


    private long mSessionPk;

    public ShopSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopInvoiceRepository.getInstance(application);
    }

    public void fetchSessionDetail() {
        mDetailData.addSource(mRepository.getShopSessionDetail(mSessionPk), mDetailData::setValue);
    }

    public void fetchRestaurantSessionReviews() {
        mShopReviewData.addSource(mRepository.getRestaurantSessionReviews(mSessionPk), mShopReviewData::setValue);
    }

    public LiveData<Resource<ShopSessionDetailModel>> getSessionDetail() {
        return mDetailData;
    }

    public LiveData<Resource<List<ShopSessionFeedbackModel>>> getSessionReviews() {
        return mShopReviewData;
    }

    @Override
    public void updateResults() {
    }

    public void setSessionPk(long sessionPk) {
        this.mSessionPk = sessionPk;
    }
}
