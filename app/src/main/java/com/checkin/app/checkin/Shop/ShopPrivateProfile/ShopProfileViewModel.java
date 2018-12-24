package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

/**
 * Created by Bhavik Patel on 24/08/2018.
 */

public class ShopProfileViewModel extends BaseViewModel{
    private ShopRepository mRepository;
    private String mShopPk;
    private MediatorLiveData<Resource<RestaurantModel>> mShopData = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mCollectAspectData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mCollectBasicData = new MutableLiveData<>();
    private MutableLiveData<JsonNode> mErrors = new MutableLiveData<>();

    public ShopProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
        mCollectBasicData.setValue(false);
        mCollectAspectData.setValue(false);
    }

    public LiveData<Resource<RestaurantModel>> getShopData() {
        return mShopData;
    }

    public RestaurantModel updateAspectData(
            CharSequence[] cuisines, CharSequence[] categories, RestaurantModel.PAYMENT_MODE[] paymentModes,
            boolean hasNonVeg, boolean hasAlcohol, boolean hasHomeDelivery, List<String> extraData) {
        Resource<RestaurantModel> resource = mShopData.getValue();
        RestaurantModel shop;
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null)
            shop = resource.data;
        else
            shop = new RestaurantModel(mShopPk);
        shop.setCuisines(cuisines);
        shop.setCategories(categories);
        shop.setPaymentModes(paymentModes);
        shop.setHasAlcohol(hasAlcohol);
        shop.setHasHomeDelivery(hasHomeDelivery);
        shop.setHasNonveg(hasNonVeg);
        shop.setExtraData(extraData.toArray(new String[]{}));

        mShopData.setValue(Resource.success(shop));
        return shop;
    }

    public RestaurantModel updateBasicData(
            String name, String website, String tagLine,
            CharSequence[] nonWorkingDays, long openingTime, long closingTime) {
        Resource<RestaurantModel> resource = mShopData.getValue();
        RestaurantModel shop;
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null)
            shop = resource.data;
        else
            shop = new RestaurantModel(mShopPk);
        shop.setName(name);
        shop.setWebsite(website);
        shop.setTagline(tagLine);
        shop.setNonWorkingDays(nonWorkingDays);
        shop.setOpeningHour(openingTime);
        shop.setClosingHour(closingTime);

        mShopData.setValue(Resource.success(shop));
        return shop;
    }

    public LiveData<Boolean> shouldCollectAspectData() {
        return mCollectAspectData;
    }

    public LiveData<Boolean> shouldCollectBasicData() {
        return mCollectBasicData;
    }

    public void updateShopContact(String phoneToken, String email) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        if (phoneToken != null)
            data.put("phone_token", phoneToken);
        if (email != null)
            data.put("email", email);
        mData.addSource(mRepository.updateShopContact(mShopPk, data), mData::setValue);
    }

    public void updateShop(RestaurantModel shop) {
        mData.addSource(mRepository.updateShopDetails(shop), mData::setValue);
    }

    public void collectData() {
        collectBasicData();
        collectAspectData();
    }

    public void collectBasicData() {
        mCollectBasicData.setValue(true);
    }

    public void collectAspectData() {
        mCollectAspectData.setValue(true);
    }

    public void showError(JsonNode data) {
        mErrors.setValue(data);
    }

    public LiveData<JsonNode> getErrors() {
        return mErrors;
    }

    public void fetchShopDetails(String shopPk) {
        mShopPk = shopPk;
        mShopData.addSource(mRepository.getShopModel(shopPk), mShopData::setValue);
    }

    public void fetchShopManage(String shopPk) {
        mShopPk = shopPk;
        mShopData.addSource(mRepository.getShopManageModel(shopPk), mShopData::setValue);
    }

    public String getShopPk() {
        return mShopPk;
    }

    public void useShop(RestaurantModel restaurantModel) {
        mShopData.setValue(Resource.success(restaurantModel));
    }

    public void postShopLogo(File logoFile){
       mData.addSource(mRepository.postShopLogo(logoFile), mData :: setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

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
