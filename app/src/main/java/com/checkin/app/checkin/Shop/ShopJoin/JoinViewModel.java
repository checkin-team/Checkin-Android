package com.checkin.app.checkin.Shop.ShopJoin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.misc.models.LocationModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopRepository;

public class JoinViewModel extends AndroidViewModel {
    private MutableLiveData<ShopJoinModel> mShopJoinData = new MutableLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mJoinResult = new MediatorLiveData<>();
    private ShopRepository mRepository;

    private boolean isRegistered = false;

    public JoinViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.Companion.getInstance(application);
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

    public RestaurantModel getNewShop(String pk) {
        isRegistered = true;
        RestaurantModel restaurantModel = new RestaurantModel(Long.valueOf(pk));
        restaurantModel.setExtraData("Night Life", "Outdoor Seating", "Host Parties");
        restaurantModel.setPaymentModes(RestaurantModel.PAYMENT_MODE.CASH);
        return restaurantModel;
    }

    public void newShopJoin(String email, String idToken) {
        ShopJoinModel shopJoinModel = new ShopJoinModel();
        shopJoinModel.setEmail(email);
        shopJoinModel.setPhoneToken(idToken);
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
