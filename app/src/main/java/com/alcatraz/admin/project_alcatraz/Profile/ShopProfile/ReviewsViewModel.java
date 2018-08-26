package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.Resource;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;
import io.objectbox.query.QueryBuilder;

public class ReviewsViewModel extends AndroidViewModel {
    ReviewsRepository repository;
    private int shopId;
    private LiveData<Resource<List<ReviewsItem>>> reviews;

    ReviewsViewModel(@NonNull Application application) {
        super(application);
        repository = ReviewsRepository.getInstance(application);
    }
    public LiveData<Resource<List<ReviewsItem>>> getReviewsItemLiveData() {
        reviews=repository.getLiveData(0L);
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