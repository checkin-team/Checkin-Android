package com.checkin.app.checkin.RestaurantImage;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

public class ImageGalleryRepository {

    public static ImageGalleryRepository INSTANCE = null;
    private WebApiService mWebApiService;

    private ImageGalleryRepository(Context context) {
        mWebApiService = ApiClient.getApiService(context);
    }

    LiveData<Resource<ImageGalleryModel>> getImageGalleryById(String reviewId){
        return new NetworkBoundResource<ImageGalleryModel,ImageGalleryModel>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ImageGalleryModel>> createCall() {
                return new RetrofitLiveData<>(mWebApiService.getImageGalleryById(reviewId));
            }

            @Override
            protected void saveCallResult(ImageGalleryModel data) {

            }
        }.getAsLiveData();
    }

    public static ImageGalleryRepository getInstance(Application application){
        if (INSTANCE == null){
            synchronized (ImageGalleryRepository.class){
                if (INSTANCE == null) {
                    INSTANCE = new ImageGalleryRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
