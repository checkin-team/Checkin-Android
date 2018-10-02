package com.checkin.app.checkin.User;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;
import io.objectbox.exception.UniqueViolationException;

public class UserRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static UserRepository INSTANCE = null;
    private Box<UserModel> mUserModel;

    private UserRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mUserModel = AppDatabase.getUserModel(context);
    }

    public LiveData<Resource<List<UserModel>>> getAllUsers() {
        return new NetworkBoundResource<List<UserModel>, List<UserModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<UserModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUsers());
            }

            @Override
            protected void saveCallResult(List<UserModel> data) {
                try {
                    mUserModel.put(data);
                } catch (UniqueViolationException e) {
                    Log.e("UserRepo", "User ID conflict!!!");
                }
            }

            @Override
            protected boolean shouldFetch(List<UserModel> data) {
                return true;
            }

            @Override
            protected LiveData<List<UserModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mUserModel.query().build());
            }
        }.getAsLiveData();
    }

    public static UserRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
