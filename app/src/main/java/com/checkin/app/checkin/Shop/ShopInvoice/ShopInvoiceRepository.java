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

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessionsById(String restaurantId, String fromDate, String toDate){
        return new NetworkBoundResource<List<RestaurantSessionModel>,List<RestaurantSessionModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantSessionModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantSessionsById(restaurantId,fromDate,toDate));
            }

            @Override
            protected void saveCallResult(List<RestaurantSessionModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ShopSessionDetailModel>> getShopSessionDetailById(String sessionId){
        return new NetworkBoundResource<ShopSessionDetailModel, ShopSessionDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopSessionDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopSessionDetailById(sessionId));
            }

            @Override
            protected void saveCallResult(ShopSessionDetailModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<ShopSessionFeedbackModel>>> getShopSessionFeedbackById(String sessionId){
        return new NetworkBoundResource<List<ShopSessionFeedbackModel>, List<ShopSessionFeedbackModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopSessionFeedbackModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopSessionFeedbackById(sessionId));
            }

            @Override
            protected void saveCallResult(List<ShopSessionFeedbackModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessionsById(String restaurantId) {
        return getRestaurantSessionsById(restaurantId, null, null);
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
