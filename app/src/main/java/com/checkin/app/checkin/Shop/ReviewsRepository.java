package com.checkin.app.checkin.Shop;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

public class ReviewsRepository extends BaseRepository {
    private static ReviewsRepository INSTANCE;
    private final WebApiService mWebService;

    private ReviewsRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<ShopReview>>> getShopReviews(String shopId){
        return new NetworkBoundResource<List<ShopReview>, List<ShopReview>>() {
            @Override
            protected void saveCallResult(@NonNull List<ShopReview> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ShopReview> data) {
                return true;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopReview>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopReviews(shopId));
            }
        }.getAsLiveData();
    }


    public static ReviewsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ReviewsRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new ReviewsRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
