package com.checkin.app.checkin.Shop;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Shop.Private.MemberModel;
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopRepository extends BaseRepository {
    private static ShopRepository INSTANCE;
    private final WebApiService mWebService;

    private ShopRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
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
                return new RetrofitLiveData<>(mWebService.putRestaurantManageDetails(restaurantModel.getPk(), restaurantModel));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> updateShopContact(final long shopId, final ObjectNode data) {
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

    public LiveData<Resource<RestaurantModel>> getShopModel(long shopId) {
        return new NetworkBoundResource<RestaurantModel, RestaurantModel>() {

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
            protected void saveCallResult(RestaurantModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RestaurantModel>> getShopManageModel(long shopId) {
        return new NetworkBoundResource<RestaurantModel, RestaurantModel>() {

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
            protected void saveCallResult(RestaurantModel data) {
            }
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

    public LiveData<Resource<List<MemberModel>>> getRestaurantMembers(long shopId) {
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

    public LiveData<Resource<ObjectNode>> addRestaurantMember(long shopId, MemberModel data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postRestaurantMember(shopId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> updateRestaurantMember(long shopId, MemberModel shopMember) {
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
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> removeRestaurantMember(long shopId, long userId) {
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
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> deleteRestaurantCover(long shopId, int index) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteRestaurantCover(shopId, index));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public Call<GenericDetailModel> postRestaurantLogo(long mShopPk, File pic, ProgressRequestBody.UploadCallbacks listener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic);
        ProgressRequestBody requestBody = new ProgressRequestBody(requestFile, listener);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("logo", "cover.jpg", requestBody);
        return mWebService.postRestaurantLogo(mShopPk, body);
    }

    public Call<GenericDetailModel> postRestaurantCover(long mShopPk, int index, File pic, ProgressRequestBody.UploadCallbacks listener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic);
        ProgressRequestBody requestBody = new ProgressRequestBody(requestFile, listener);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("image", "cover.jpg", requestBody);
        return mWebService.postRestaurantCover(mShopPk, index, body);
    }
}
