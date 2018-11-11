package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.ShopRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class MemberViewModel extends BaseViewModel {
    private ShopRepository mRepository;
    private String mShopPk;
    private MediatorLiveData<Resource<List<MemberModel>>> mShopMembers = new MediatorLiveData<>();

    public MemberViewModel(@NonNull Application application) {
        super(application);

        mRepository = ShopRepository.getInstance(application);

    }

    public void fetchShopMembers(String shopPk) {
        mShopPk = shopPk;
        mShopMembers.addSource(mRepository.getRestaurantMembers(shopPk), mShopMembers::setValue);
    }

    public String getShopPk() {
        return mShopPk;
    }

    public void setShopMembers(List<MemberModel> shopMembers)
    {
        mData.addSource(mRepository.postRestaurantMembers(getShopPk(),shopMembers), mData::setValue);
    }

    public LiveData<Resource<List<MemberModel>>> getShopMembers() {
        return mShopMembers;
    }



    @Override
    public void updateResults() {

    }
}
