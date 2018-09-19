package com.checkin.app.checkin.Shop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

public class ReviewsViewModel extends BaseViewModel {
    private ReviewsRepository mRepository;
    private LiveData<Resource<List<ShopReview>>> mReviewsData;

    ReviewsViewModel(@NonNull Application application) {
        super(application);
        mRepository = ReviewsRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public LiveData<Resource<List<ShopReview>>> getShopReviews(long shopId) {
        if (mReviewsData == null)
            mReviewsData = mRepository.getShopReviews(String.valueOf(shopId));
        return mReviewsData;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ReviewsViewModel(mApplication);
        }
    }
}