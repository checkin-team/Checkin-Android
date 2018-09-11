package com.checkin.app.checkin.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

class ShopProfileViewModel extends BaseViewModel{
    private ShopProfileRepository mRepository;

    public ShopProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopProfileRepository.getInstance(application);
    }

    public LiveData<Resource<List<ShopHomeModel>>> getShopHomeModel(int shopHomeId) {
        return mRepository.getShopHomeModel(shopHomeId);
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
            return (T) new ShopProfileViewModel(mApplication);
        }
    }

    @Override
    public void updateResults() {

    }
}
