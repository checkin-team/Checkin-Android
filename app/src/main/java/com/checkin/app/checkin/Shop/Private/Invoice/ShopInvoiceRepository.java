package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class ShopInvoiceRepository {
    private static ShopInvoiceRepository INSTANCE;
    private final WebApiService mWebService;

    public ShopInvoiceRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
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

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessions(long restaurantId, String fromDate, String toDate) {
        return new NetworkBoundResource<List<RestaurantSessionModel>, List<RestaurantSessionModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantSessionModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantSessionsById(restaurantId, fromDate, toDate));
            }

            @Override
            protected void saveCallResult(List<RestaurantSessionModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ShopSessionDetailModel>> getShopSessionDetail(long sessionId) {
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

    public LiveData<Resource<List<RestaurantSessionModel>>> getRestaurantSessions(long restaurantId) {
        return getRestaurantSessions(restaurantId, null, null);
    }
}
