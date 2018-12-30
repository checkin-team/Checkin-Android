package com.checkin.app.checkin.Shop.ShopReview;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.WebApiService;

public class ShopReviewRepository extends BaseRepository {
    private static ShopReviewRepository INSTANCE;
    private final WebApiService mWebService;

    private ShopReviewRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ShopReviewRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopReviewRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new ShopReviewRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
