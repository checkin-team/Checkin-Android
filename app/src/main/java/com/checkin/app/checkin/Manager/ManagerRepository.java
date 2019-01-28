package com.checkin.app.checkin.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

public class ManagerRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static ManagerRepository INSTANCE;

    private ManagerRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<RestaurantStaticsModel>> getRestaurantStaticsById(String restaurantId){
        return new NetworkBoundResource<RestaurantStaticsModel, RestaurantStaticsModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantStaticsModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantStaticsById(restaurantId));
            }

            @Override
            protected void saveCallResult(RestaurantStaticsModel data) {

            }
        }.getAsLiveData();
    }

    public static ManagerRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ManagerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ManagerRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
