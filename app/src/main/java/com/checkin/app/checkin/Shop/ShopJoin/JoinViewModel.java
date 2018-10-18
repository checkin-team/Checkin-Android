package com.checkin.app.checkin.Shop.ShopJoin;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.Shop.ShopRepository;

public class JoinViewModel extends BaseViewModel {
    private static final String TAG = JoinViewModel.class.getSimpleName();
    private MutableLiveData<ShopJoinModel> mShopLiveModel = new MutableLiveData<>();
    private ShopRepository mRepository;

    public JoinViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
    }

    public void registerNewBusiness() {
        ShopJoinModel model = mShopLiveModel.getValue();
        mData.addSource(mRepository.registerShop(model), mData::setValue);
    }

    public LiveData<ShopJoinModel> getShopLiveModel() {
        return mShopLiveModel;
    }

    public void newShop(String email, String idToken) {
        Log.e(TAG, "EMAIL: " + email + "ID Token: " + idToken);
        ShopJoinModel shopJoinModel = new ShopJoinModel();
        shopJoinModel.setEmail(email);
        shopJoinModel.setIdToken(idToken);
        mShopLiveModel.setValue(shopJoinModel);
    }

    public void updateShop(String name, String gstin, String locality) {
        ShopJoinModel shopJoinModel = mShopLiveModel.getValue();
        if (shopJoinModel != null) {
            shopJoinModel.setName(name);
            shopJoinModel.setGstin(gstin);
            shopJoinModel.setLocality(locality);
            mShopLiveModel.setValue(shopJoinModel);
        }
    }

    public void setLocation(LocationModel location) {
        ShopJoinModel shopJoinModel = mShopLiveModel.getValue();
        if (shopJoinModel != null) {
            shopJoinModel.setLocation(location);
            mShopLiveModel.setValue(shopJoinModel);
        }
    }

    @Override
    public void updateResults() {

    }
}
