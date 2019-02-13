package com.checkin.app.checkin.Review.ShopReview;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Review.ReviewRepository;

import java.util.List;

public class ShopReviewViewModel extends BaseViewModel {
    private ReviewRepository mRepository;

    private long mShopPk;
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

    public void fetchShopReviews(long shopPk) {
        mShopPk = shopPk;
        mShopReviewData.addSource(mRepository.getRestaurantReviews(shopPk), mShopReviewData::setValue);
    }

    public void reactToReview(ShopReviewModel reviewModel) {
        mData.addSource(mRepository.postReviewReact(reviewModel.getPk()), mData::setValue);
    }
}
