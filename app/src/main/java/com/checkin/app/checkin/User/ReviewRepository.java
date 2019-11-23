package com.checkin.app.checkin.User;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.User.bills.NewReviewModel;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ReviewRepository {
    private final WebApiService mWebService;
    private static ReviewRepository INSTANCE = null;

    private ReviewRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static ReviewRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReviewRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /*public LiveData<Resource<RestaurantBriefModel>> getRestaurantBrief(final String restaurantId) {
        return new NetworkBoundResource<RestaurantBriefModel, RestaurantBriefModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantBriefModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantBrief(restaurantId));
            }

            @Override
            protected void saveCallResult(RestaurantBriefModel data) {
            }
        }.getAsLiveData();
    }*/

    public LiveData<Resource<ObjectNode>> postSessionReview(long sessionId, NewReviewModel data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerReview(sessionId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }



}
