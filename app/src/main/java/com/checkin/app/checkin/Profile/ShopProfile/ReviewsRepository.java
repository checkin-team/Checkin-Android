package com.checkin.app.checkin.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

public class ReviewsRepository extends BaseRepository {
    private static ReviewsRepository INSTANCE;
    private WebApiService mWebService;
    private Box<ReviewsItem> reviews;
    private ReviewsRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        reviews = AppDatabase.getReviewsModel(context);
    }
    public LiveData<Resource<List<ReviewsItem>>> getLiveData(long reviewId){
        return new NetworkBoundResource<List<ReviewsItem>, List<ReviewsItem>>() {


            @Override
            protected void saveCallResult(@NonNull List<ReviewsItem> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ReviewsItem> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<ReviewsItem>> loadFromDb() {
                return new ObjectBoxLiveData<>(reviews.query().order(ReviewsItem_.name).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ReviewsItem>>> createCall() {
                return null;
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
