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

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopRepository extends BaseRepository {

    private final WebApiService mWebService;
//    private Box<RestaurantModel> mShopModel;
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

    public LiveData<Resource<ObjectNode>> updateShopDetails(final RestaurantModel restaurantModel) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postRestaurantManageDetails(restaurantModel.getId(), restaurantModel));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RestaurantModel>> getShopModel(String shopId) {
        return new NetworkBoundResource<RestaurantModel, RestaurantModel> () {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantDetails(shopId));
            }

            @Override
            protected void saveCallResult(RestaurantModel data) {  }
        }.getAsLiveData();
    }
    public LiveData<Resource<RestaurantModel>> getShopManageModel(String shopId) {
        return new NetworkBoundResource<RestaurantModel, RestaurantModel> () {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantManageDetails(shopId));
            }

            @Override
            protected void saveCallResult(RestaurantModel data) {  }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RestaurantModel>>> getShops() {
        return new NetworkBoundResource<List<RestaurantModel>, List<RestaurantModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurants());
            }

            @Override
            protected void saveCallResult(List<RestaurantModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postShopLogo(File pic) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("profile_pic", "profile.jpg", requestFile);
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postShopLogo(body));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }
}
