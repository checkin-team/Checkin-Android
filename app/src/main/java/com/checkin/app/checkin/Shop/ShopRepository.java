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
import com.checkin.app.checkin.Shop.ShopPrivateProfile.MemberModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

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
                return new RetrofitLiveData<>(mWebService.putRestaurantManageDetails(restaurantModel.getId(), restaurantModel));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> updateShopContact(final String shopId, final ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.putRestaurantContactVerify(shopId, data));
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

    public LiveData<Resource<List<MemberModel>>> getRestaurantMembers(String shopId) {
        return new NetworkBoundResource<List<MemberModel>, List<MemberModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MemberModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantMembers(shopId));
            }

            @Override
            protected void saveCallResult(List<MemberModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> addRestaurantMember(String shopId, MemberModel data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postRestaurantMember(shopId,data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) { }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> updateRestaurantMember(String shopId, MemberModel shopMember) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.putRestaurantMember(shopId, shopMember.getUserId(), shopMember));
            }

            @Override
            protected void saveCallResult(ObjectNode data) { }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> removeRestaurantMember(String shopId, String userId) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteRestaurantMember(shopId, userId));
            }

            @Override
            protected void saveCallResult(ObjectNode data) { }
        }.getAsLiveData();
    }

}
