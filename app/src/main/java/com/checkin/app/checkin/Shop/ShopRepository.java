package com.checkin.app.checkin.Shop;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.ObjectBoxInstanceLiveData;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import io.objectbox.Box;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopRepository extends BaseRepository {

    private final WebApiService mWebService;
    private Box<ShopModel> mShopModel;
    private static ShopRepository INSTANCE;

    private ShopRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mShopModel = AppDatabase.getShopModel(context);
    }

    public static ShopRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShopRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<ShopModel>> getShopModel(long shopId) {
        return new NetworkBoundResource<ShopModel, ShopModel> () {

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @Override
            protected LiveData<ShopModel> loadFromDb() {
                return new ObjectBoxInstanceLiveData<>(mShopModel.query().equal(ShopModel_.id, shopId).build());
            }

            @Override
            protected boolean shouldFetch(ShopModel data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopDetails(shopId));
            }

            @Override
            protected void saveCallResult(ShopModel data) {
                mShopModel.put(data);
            }
        }.getAsLiveData();

    }
}
