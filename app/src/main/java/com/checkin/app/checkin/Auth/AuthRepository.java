package com.checkin.app.checkin.Auth;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

import javax.inject.Singleton;

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
