package com.checkin.app.checkin.Manager;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.WebApiService;

public class ManagerRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static ManagerRepository INSTANCE;

    private ManagerRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ManagerRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ManagerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ManagerRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
