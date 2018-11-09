package com.checkin.app.checkin.Home;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class HomeViewModel extends BaseViewModel {
    private HomeRepository mRepository;
    private ShopRepository mShopRepository;

    HomeViewModel(Application application) {
        super(application);
        mRepository = HomeRepository.getInstance(application);
        mShopRepository = ShopRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public void decryptQr(String data) {
        ObjectNode requestJson = Converters.objectMapper.createObjectNode();
        requestJson.put("data", data);
        mData.addSource(mRepository.postDecryptQr(requestJson), mData::setValue);
    }

    public LiveData<Resource<List<RestaurantModel>>> getTrendingRestaurants() {
        return mShopRepository.getShops();
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
            return (T) new HomeViewModel(mApplication);
        }
    }
}
