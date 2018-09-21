package com.checkin.app.checkin.User;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.WebApiService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

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
                return null;
            }

            @Override
            protected void saveCallResult(List<UserModel> data) {

            }

            @Override
            protected boolean shouldFetch(List<UserModel> data) {
                return false;
            }

            @Override
            protected LiveData<List<UserModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mUserModel.query().build());
            }
        }.getAsLiveData();
    }

    public void postImage(File rectangleFile,long userId)
    {
        RequestBody request = RequestBody.create(MediaType.parse("image/jpeg"), rectangleFile);

        MultipartBody.Part body =MultipartBody.Part.createFormData("picture", "Rectangle Image", request);
        Call<ResponseBody> call = mWebService.postProfileImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });

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
