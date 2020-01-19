package com.checkin.app.checkin.Shop.Private.Insight;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.data.BaseRepository;
import com.checkin.app.checkin.data.network.ApiClient;
import com.checkin.app.checkin.data.network.ApiResponse;
import com.checkin.app.checkin.data.network.RetrofitLiveData;
import com.checkin.app.checkin.data.network.WebApiService;
import com.checkin.app.checkin.data.resource.NetworkBoundResource;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.session.models.PromoDetailModel;

import java.util.List;

public class ShopInsightRepository extends BaseRepository {

    private static ShopInsightRepository INSTANCE;
    private final WebApiService mWebService;

    private ShopInsightRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static ShopInsightRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopInsightRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShopInsightRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<ShopInsightRevenueModel>> getShopInsightRevenue(long shopId) {
        return new NetworkBoundResource<ShopInsightRevenueModel, ShopInsightRevenueModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopInsightRevenueModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopInsightRevenueDetail(shopId));
            }

            @Override
            protected void saveCallResult(ShopInsightRevenueModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ShopInsightLoyaltyProgramModel>> getShopInsightLoyaltyProgram(long shopId) {
        return new NetworkBoundResource<ShopInsightLoyaltyProgramModel, ShopInsightLoyaltyProgramModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopInsightLoyaltyProgramModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopInsightLoyaltyDetail(shopId));
            }

            @Override
            protected void saveCallResult(ShopInsightLoyaltyProgramModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<PromoDetailModel>>> getShopActivePromos(final long shopId) {
        return new NetworkBoundResource<List<PromoDetailModel>, List<PromoDetailModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<PromoDetailModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantActivePromos(shopId, null));
            }

            @Override
            protected void saveCallResult(List<PromoDetailModel> data) {
            }
        }.getAsLiveData();
    }
}
