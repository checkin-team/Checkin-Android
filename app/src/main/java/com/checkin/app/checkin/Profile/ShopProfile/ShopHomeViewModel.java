package com.checkin.app.checkin.Profile.ShopProfile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.ShopModel;

import java.util.List;

/**
 * Created by Bhavik Patel on 19/08/2018.
 */

public class ShopHomeViewModel extends AndroidViewModel {

    private final ShopHomeRepository mRepository;
    //private LiveData<Resource<ShopHomeModel>> mData;

    public ShopHomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopHomeRepository.getInstance(application);
    }

    public LiveData<Resource<List<ShopModel>>> getShopHomeModel(int shopHomeId) {
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
            return (T) new ShopHomeViewModel(mApplication);
        }
    }
}
