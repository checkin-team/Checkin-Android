package com.checkin.app.checkin.ManagerProfile;

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
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;

import java.util.List;

public class RestaurantTableRepository {

    private static RestaurantTableRepository INSTANCE;
    private WebApiService mWebApiService;

    private RestaurantTableRepository(Context context){
        mWebApiService = ApiClient.getApiService(context);
    }

    LiveData<Resource<List<RestaurantTableModel>>> getRestaurantTableById(String restaurantId){
        return new NetworkBoundResource<List<RestaurantTableModel>,List<RestaurantTableModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantTableModel>>> createCall() {
                return new RetrofitLiveData<>(mWebApiService.getRestaurantTableById(restaurantId));
            }

            @Override
            protected void saveCallResult(List<RestaurantTableModel> data) {

            }
        }.getAsLiveData();
    }

    public static RestaurantTableRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (RestaurantTableRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RestaurantTableRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
