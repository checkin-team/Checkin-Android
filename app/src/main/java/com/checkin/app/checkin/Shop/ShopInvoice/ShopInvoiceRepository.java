package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

public class ShopInvoiceRepository {
    private final WebApiService mWebService;
    private static ShopInvoiceRepository INSTANCE;

    public ShopInvoiceRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<RestaurantResponseModel>>> getRestaurantById(String restaurant_id){
        return new NetworkBoundResource<List<RestaurantResponseModel>,List<RestaurantResponseModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantResponseModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantsById(restaurant_id));
            }

            @Override
            protected void saveCallResult(List<RestaurantResponseModel> data) {

            }
        }.getAsLiveData();
    }

    public static ShopInvoiceRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopInvoiceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShopInvoiceRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
