package com.checkin.app.checkin.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Shop.ShopModel_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

/**
 * Created by Bhavik Patel on 19/08/2018.
 */

public class ShopHomeRepository extends BaseRepository {

    private final WebApiService mWebService;
    private Box<ShopModel> mShopHomeModel;
    private static ShopHomeRepository INSTANCE;

    private ShopHomeRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mShopHomeModel = AppDatabase.getShopModel(context);
    }

    public static ShopHomeRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShopHomeRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShopHomeRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<ShopModel>>> getShopHomeModel(int shopHomeId) {
        return new NetworkBoundResource<List<ShopModel>,List<ShopModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @Override
            protected LiveData<List<ShopModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mShopHomeModel.query().equal(ShopModel_.id, shopHomeId).build());
            }

            @Override
            protected boolean shouldFetch(List<ShopModel> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopModel>>> createCall() {
                return null;
            }

            @Override
            protected void saveCallResult(List<ShopModel> data) {

            }
        }.getAsLiveData();

    }
}
