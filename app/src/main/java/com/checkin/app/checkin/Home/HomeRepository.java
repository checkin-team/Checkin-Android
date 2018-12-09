package com.checkin.app.checkin.Home;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.WebApiService;

import javax.inject.Singleton;

@Singleton
public class HomeRepository extends BaseRepository {
    private static HomeRepository INSTANCE;
    private WebApiService mWebService;

    public HomeRepository() {}

    private HomeRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static HomeRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (HomeRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new HomeRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
