package com.checkin.app.checkin.Shop;

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
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopRepository extends BaseRepository {

    private final WebApiService mWebService;
//    private Box<ShopModel> mShopModel;
    private static ShopRepository INSTANCE;

    private ShopRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
//        mShopModel = AppDatabase.getShopModel(context);
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

    public LiveData<Resource<GenericDetailModel>> registerShop(ShopJoinModel model) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postRegisterShop(model));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> updateShopDetails(final ShopModel shopModel) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postShopManageDetails(shopModel.getId(), shopModel));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ShopModel>> getShopModel(String shopId) {
        return new NetworkBoundResource<ShopModel, ShopModel> () {

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ShopModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getShopDetails(shopId));
            }

            @Override
            protected void saveCallResult(ShopModel data) {  }
        }.getAsLiveData();
    }
}
