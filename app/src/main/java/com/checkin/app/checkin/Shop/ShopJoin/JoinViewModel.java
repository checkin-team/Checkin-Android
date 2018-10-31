package com.checkin.app.checkin.Shop.ShopJoin;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Shop.ShopRepository;

public class JoinViewModel extends AndroidViewModel {
    private static final String TAG = JoinViewModel.class.getSimpleName();
    private MutableLiveData<ShopJoinModel> mShopJoinData = new MutableLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mJoinResult = new MediatorLiveData<>();
    private ShopRepository mRepository;

    private boolean isRegistered = false;

    public JoinViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
    }

    public void registerNewBusiness() {
        ShopJoinModel model = mShopJoinData.getValue();
        mJoinResult.addSource(mRepository.registerShop(model), mJoinResult::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getJoinResults() {
        return mJoinResult;
    }

    public LiveData<ShopJoinModel> getShopJoinModel() {
        return mShopJoinData;
    }

    public ShopModel getNewShop(String pk) {
        isRegistered = true;
        ShopModel shopModel = new ShopModel(pk);
        shopModel.setExtraData("Night Life", "Outdoor Seating", "Host Parties");
        shopModel.setPaymentModes(ShopModel.PAYMENT_MODE.CASH);
        return shopModel;
    }

    public void newShopJoin(String email, String idToken) {
        Log.e(TAG, "EMAIL: " + email + "ID Token: " + idToken);
        ShopJoinModel shopJoinModel = new ShopJoinModel();
        shopJoinModel.setEmail(email);
        shopJoinModel.setIdToken(idToken);
        mShopJoinData.setValue(shopJoinModel);
    }

    public void updateShopJoin(String name, String gstin, String locality) {
        ShopJoinModel shopJoinModel = mShopJoinData.getValue();
        if (shopJoinModel != null) {
            shopJoinModel.setName(name);
            shopJoinModel.setGstin(gstin);
            shopJoinModel.setLocality(locality);
            mShopJoinData.setValue(shopJoinModel);
        }
    }

    public void setLocation(LocationModel location) {
        ShopJoinModel shopJoinModel = mShopJoinData.getValue();
        if (shopJoinModel != null) {
            shopJoinModel.setLocation(location);
            mShopJoinData.setValue(shopJoinModel);
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}
