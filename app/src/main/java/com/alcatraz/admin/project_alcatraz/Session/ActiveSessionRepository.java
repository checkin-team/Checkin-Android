package com.alcatraz.admin.project_alcatraz.Session;

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

import io.objectbox.Box;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionRepository extends BaseRepository {

    private final WebApiService mWebService;
    private static ActiveSessionRepository INSTANCE;

    private ActiveSessionRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ActiveSessionRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ActiveSessionRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActiveSessionRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }


    public LiveData<Resource<ActiveSessionModel>> getActiveSessionModel(int activeSessionId) {
        return new NetworkBoundResource<ActiveSessionModel, ActiveSessionModel>() {

            @Override
            protected void saveCallResult(ActiveSessionModel data) {}

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActiveSessionModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSession(activeSessionId));
            }
        }.getAsLiveData();
    }
}
