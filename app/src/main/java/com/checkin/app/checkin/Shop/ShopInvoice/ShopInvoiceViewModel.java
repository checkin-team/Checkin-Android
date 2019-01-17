package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

public class ShopInvoiceViewModel extends BaseViewModel {
    private ShopInvoiceRepository mShopInvoiceRepository;

    private String restaurantId;
    private LiveData<Resource<List<RestaurantSessionModel>>> mPrevResults;
    private MediatorLiveData<Resource<List<RestaurantSessionModel>>> mResults = new MediatorLiveData<>();

    public ShopInvoiceViewModel(@NonNull Application application) {
        super(application);
        this.mShopInvoiceRepository = ShopInvoiceRepository.getInstance(application);
    }

    void getRestaurantSessionsById(String restaurant_id) {
        restaurantId = restaurant_id;
        mPrevResults = mShopInvoiceRepository.getRestaurantSessionsById(restaurant_id);
        mResults.addSource(mPrevResults, mResults::setValue);
    }

    void filterRestaurantSessions(String fromDate, String toDate) {
        if (mPrevResults != null)
            mResults.removeSource(mPrevResults);
        mPrevResults = mShopInvoiceRepository.getRestaurantSessionsById(restaurantId, fromDate, toDate);
        mResults.addSource(mPrevResults, mResults::setValue);
    }

    LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessions() {
        return mResults;
    }

    @Override
    public void updateResults() {
    }
}
