package com.checkin.app.checkin.Review;

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
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ReviewRepository {
    private final WebApiService mWebService;
    private static ReviewRepository INSTANCE;

    private ReviewRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<ShopReviewModel>>> getRestaurantReviews(final String shopPk) {
        return new NetworkBoundResource<List<ShopReviewModel>, List<ShopReviewModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopReviewModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantReviews(shopPk));
            }

            @Override
            protected void saveCallResult(List<ShopReviewModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postReviewReact(final String reviewPk) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postReviewReact(reviewPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }


    public static ReviewRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ReviewRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReviewRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
