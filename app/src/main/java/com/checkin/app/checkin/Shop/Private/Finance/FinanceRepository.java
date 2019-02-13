package com.checkin.app.checkin.Shop.Private.Finance;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Misc.GenericDetailModel;

public class FinanceRepository {

    private final WebApiService mWebService;
    private static FinanceRepository INSTANCE;

    public FinanceRepository(Context context){
        mWebService = ApiClient.getApiService(context);
    }

    public static FinanceRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (FinanceRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FinanceRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<FinanceModel>> getRestaurantFinanceById(long restaurantId){
        return new NetworkBoundResource<FinanceModel, FinanceModel>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<FinanceModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantFinanceById(restaurantId));
            }

            @Override
            protected void saveCallResult(FinanceModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> setRestaurantFinanceById(FinanceModel financeModel, long restaurantId){
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.setRestaurantFinanceById(financeModel,restaurantId));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {

            }
        }.getAsLiveData();
    }
}
