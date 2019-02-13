package com.checkin.app.checkin.Review;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Review.NewReview.NewReviewModel;
import com.checkin.app.checkin.Review.NewReview.RestaurantBriefModel;
import com.checkin.app.checkin.Review.NewReview.ReviewImageModel;
import com.checkin.app.checkin.Review.NewReview.ReviewImageShowModel;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ReviewRepository {
    private final WebApiService mWebService;
    private static ReviewRepository INSTANCE;

    private ReviewRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }
    private MediatorLiveData<Resource<ReviewImageShowModel>> mReviewImageShowModel = new MediatorLiveData<>();
//    private final LiveData<List<ReviewImageShowModel>> mSelectedImage;

    public LiveData<Resource<List<ShopReviewModel>>> getRestaurantReviews(final long shopPk) {
        return new NetworkBoundResource<List<ShopReviewModel>, List<ShopReviewModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopReviewModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantReviews(shopPk));
            }

            @Override
            protected void saveCallResult(List<ShopReviewModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postReviewReact(final long reviewPk) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postReviewReact(reviewPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RestaurantBriefModel>> getRestaurantBrief(final String restaurantId) {
        return new NetworkBoundResource<RestaurantBriefModel, RestaurantBriefModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RestaurantBriefModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantBrief(restaurantId));
            }

            @Override
            protected void saveCallResult(RestaurantBriefModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postSessionReview(String sessionPk, NewReviewModel data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerReview(sessionPk, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> postReviewImage(File pic, ReviewImageModel data) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("image", pic.getName(), requestFile);


        RequestBody useCaseData = RequestBody.create(MediaType.parse("application/json"), data.getUseCase());
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerReviewPic(body, useCaseData));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> deleteSelectedImage(final String imagePk) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteImage(imagePk));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {

            }
        }.getAsLiveData();
    }

    public static ReviewRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ReviewRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReviewRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
