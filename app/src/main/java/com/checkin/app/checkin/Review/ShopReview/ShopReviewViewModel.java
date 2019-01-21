package com.checkin.app.checkin.Review.ShopReview;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Review.ReviewRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

public class ShopReviewViewModel extends BaseViewModel {
    private ReviewRepository mRepository;

    private String mShopPk;
    private MediatorLiveData<Resource<List<ShopReviewModel>>> mShopReviewData = new MediatorLiveData<>();

    public ShopReviewViewModel(@NonNull Application application) {
        super(application);
        mRepository = ReviewRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchShopReviews(mShopPk);
    }

    public LiveData<Resource<List<ShopReviewModel>>> getReviewData() {
        return mShopReviewData;
    }

    public void fetchShopReviews(String shopPk) {
        mShopPk = shopPk;
        mShopReviewData.addSource(mRepository.getRestaurantReviews(shopPk), mShopReviewData::setValue);
    }

    public void reactToReview(ShopReviewModel reviewModel) {
        mData.addSource(mRepository.postReviewReact(reviewModel.getPk()), mData::setValue);
    }

    public void register(@NonNull String body, @Nullable int food_rating, @NonNull int ambience_rating, @NonNull int hospitality_rating) {
        Log.e("rating",food_rating + " ==" + ambience_rating + " == "+ hospitality_rating);
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("body", body);
        data.put("food_rating", food_rating);
        data.put("ambience_rating", ambience_rating);
        data.put("hospitality_rating", hospitality_rating);
        data.put("image_pks", "");
        mData.addSource(mRepository.postReview(data), mData::setValue);
    }

    public void uploadReviewImage(File pictureFile) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("use_case ", "review.food");
        mData.addSource(mRepository.postReviewImage(pictureFile,data), mData::setValue);
    }
}
