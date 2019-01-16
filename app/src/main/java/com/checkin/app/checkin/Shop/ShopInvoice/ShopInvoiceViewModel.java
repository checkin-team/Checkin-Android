package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.Application;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;

import java.util.List;

public class ShopInvoiceViewModel extends BaseViewModel {

    private ShopInvoiceRepository mShopInvoiceRepository;
    private MediatorLiveData<Resource<List<RestaurantResponseModel>>> mResourceMediatorLiveData = new MediatorLiveData<>();

    public ShopInvoiceViewModel(@NonNull Application application) {
        super(application);
        this.mShopInvoiceRepository = ShopInvoiceRepository.getInstance(application);
    }

    public void fetchRestaurantId(String restaurant_id) {
        mResourceMediatorLiveData.addSource(mShopInvoiceRepository.getRestaurantById(restaurant_id), mResourceMediatorLiveData::setValue);
    }

    @Override
    public void updateResults() {
    }
}
