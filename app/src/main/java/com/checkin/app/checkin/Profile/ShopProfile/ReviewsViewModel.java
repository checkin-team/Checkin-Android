package com.checkin.app.checkin.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.Resource;

import java.util.List;

public class ReviewsViewModel extends AndroidViewModel {
    ReviewsRepository repository;
    private LiveData<Resource<List<ReviewsItem>>> reviews;

    ReviewsViewModel(@NonNull Application application) {
        super(application);
        repository = ReviewsRepository.getInstance(application);
    }
    public LiveData<Resource<List<ReviewsItem>>> getReviewsItemLiveData(long shopid) {
        reviews=repository.getLiveData(shopid);
        return reviews;
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