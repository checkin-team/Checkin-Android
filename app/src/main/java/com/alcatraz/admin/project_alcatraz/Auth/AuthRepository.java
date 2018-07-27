package com.alcatraz.admin.project_alcatraz.Auth;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.ApiResponse;
import com.alcatraz.admin.project_alcatraz.Data.BaseRepository;
import com.alcatraz.admin.project_alcatraz.Data.NetworkBoundResource;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.RetrofitLiveData;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class AuthRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static AuthRepository INSTANCE;

    private AuthRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<Map<String, String>>> login(final Map<String, String> credentials) {
        return new NetworkBoundResource<Map<String, String>, Map<String, String>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Map<String, String>>> createCall() {
                return new RetrofitLiveData<>(mWebService.postLogin(credentials));
            }

            @Override
            protected void saveCallResult(Map<String, String> data) {
            }
        } .getAsLiveData();
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
