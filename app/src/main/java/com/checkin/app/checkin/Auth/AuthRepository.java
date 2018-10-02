package com.checkin.app.checkin.Auth;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class AuthRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static AuthRepository INSTANCE;

    private AuthRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<ObjectNode>> login(final ObjectNode credentials) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postLogin(credentials));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> register(final ObjectNode credentials) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postRegister(credentials));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public Call<ObjectNode> postDeviceToken(String token) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("device_token", token);
        return mWebService.postFCMToken(data);
    }

    public static AuthRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AuthRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
