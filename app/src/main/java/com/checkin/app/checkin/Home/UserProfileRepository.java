package com.checkin.app.checkin.Home;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Profile.ShopProfile.ReviewsItem;
import com.checkin.app.checkin.Profile.ShopProfile.ReviewsItem_;
import com.checkin.app.checkin.Profile.ShopProfile.ReviewsRepository;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

public class UserProfileRepository extends BaseRepository {

    private static UserProfileRepository INSTANCE;
    private WebApiService mWebService;
    private Box<UserProfileEntity> userBox;
    private UserProfileRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        userBox = AppDatabase.getUserProfileModel(context);
    }
    public LiveData<Resource<List<UserProfileEntity>>> getLiveData(long userId){
        return new NetworkBoundResource<List<UserProfileEntity>, List<UserProfileEntity>>() {


            @Override
            protected void saveCallResult(@NonNull List<UserProfileEntity> item) {
            }



            @Override
            protected boolean shouldFetch(@Nullable List<UserProfileEntity> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<UserProfileEntity>> loadFromDb() {
                return new ObjectBoxLiveData<>(userBox.query().build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<UserProfileEntity>>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }


    public static UserProfileRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserProfileRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new UserProfileRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
