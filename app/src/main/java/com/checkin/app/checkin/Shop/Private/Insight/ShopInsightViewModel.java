package com.checkin.app.checkin.Shop.Private.Insight;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.model.PromoDetailModel;

import java.util.List;

public class ShopInsightViewModel extends BaseViewModel {
    private ShopInsightRepository mRepository;
    private long mShopId;

    private SourceMappedLiveData<Resource<ShopInsightRevenueModel>> mRevenueData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ShopInsightLoyaltyProgramModel>> mLoyaltyData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<PromoDetailModel>>> mActivePromosData = createNetworkLiveData();


    public ShopInsightViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopInsightRepository.getInstance(application);
    }

    public void fetchShopInsightRevenueDetail(long shopId) {
        mShopId = shopId;
        mRevenueData.addSource(mRepository.getShopInsightRevenue(mShopId), mRevenueData::setValue);
    }

    public LiveData<Resource<ShopInsightRevenueModel>> getInsightRevenueDetail() {
        return mRevenueData;
    }

    public void fetchShopInsightLoyaltyDetail() {
        mLoyaltyData.addSource(mRepository.getShopInsightLoyaltyProgram(mShopId), mLoyaltyData::setValue);
    }

    public void fetchShopActivePromos() {
        mActivePromosData.addSource(mRepository.getShopActivePromos(mShopId), mActivePromosData::setValue);
    }

    public LiveData<Resource<List<PromoDetailModel>>> getShopActivePromos() {
        return mActivePromosData;
    }

    public LiveData<Resource<ShopInsightLoyaltyProgramModel>> getInsightLoyaltyDetail() {
        return mLoyaltyData;
    }

    @Override
    public void updateResults() {

    }
}
