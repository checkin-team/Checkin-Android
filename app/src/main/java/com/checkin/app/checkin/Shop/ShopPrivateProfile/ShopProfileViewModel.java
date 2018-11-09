package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopRepository;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopProfileViewModel extends BaseViewModel{
    private ShopRepository mRepository;
    private String mShopPk;
    private MediatorLiveData<Resource<RestaurantModel>> mShopData = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mCollectData = new MutableLiveData<>();
    private MutableLiveData<JsonNode> mErrors = new MutableLiveData<>();

    public ShopProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
        mCollectData.setValue(false);
    }

    public LiveData<Resource<RestaurantModel>> getShopData() {
        return mShopData;
    }

    public RestaurantModel updateAspectData(
            CharSequence[] cuisines, CharSequence[] categories, RestaurantModel.PAYMENT_MODE[] paymentModes,
            boolean hasNonVeg, boolean hasAlcohol, boolean hasHomeDelivery, List<String> extraData) {
        Resource<RestaurantModel> resource = mShopData.getValue();
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
            RestaurantModel shop = resource.data;
            shop.setCuisines(cuisines);
            shop.setCategories(categories);
            shop.setPaymentModes(paymentModes);
            shop.setHasAlcohol(hasAlcohol);
            shop.setHasHomeDelivery(hasHomeDelivery);
            shop.setHasNonveg(hasNonVeg);
            shop.setExtraData(extraData.toArray(new String[]{}));
            return shop;
        }
        return null;
    }

    public LiveData<Boolean> shouldCollectData() {
        return mCollectData;
    }

    public void updateShop(RestaurantModel shop) {
        mData.addSource(mRepository.updateShopDetails(shop), mData::setValue);
    }

    public void collectData() {
        mCollectData.setValue(true);
    }

    public void showError(JsonNode data) {
        mErrors.setValue(data);
    }

    public LiveData<JsonNode> getErrors() {
        return mErrors;
    }

    public void fetchShop(String shopPk) {
        mShopPk = shopPk;
        mShopData.addSource(mRepository.getShopModel(shopPk), mShopData::setValue);
    }

    public String getShopPk() {
        return mShopPk;
    }

    public void useShop(RestaurantModel restaurantModel) {
        mShopData.setValue(Resource.success(restaurantModel));
    }

    @Override
    public void updateResults() {

    }
}
