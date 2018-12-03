package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

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

    public void fetchShopMembers() {

        mShopMembers.addSource(mRepository.getRestaurantMembers(mShopPk), mShopMembers::setValue);
    }

    public void setShopPk(String mShopPk) {
        this.mShopPk = mShopPk;
    }

    public String getShopPk() {
        return mShopPk;
    }

    public void addShopMember(MemberModel shopMember)
    {
        mData.addSource(mRepository.postRestaurantMember(getShopPk(),shopMember), mData::setValue);
    }
    public void updateShopMember(MemberModel shopMember)
    {
        mData.addSource(mRepository.updateRestaurantMember(getShopPk(),shopMember.getUser().getPk(),shopMember), mData::setValue);
    }
    public void updateShopMemberPartially(MemberModel shopMember)
    {
        mData.addSource(mRepository.updateRestaurantMemberPartial(getShopPk(),shopMember.getUser().getPk(),shopMember), mData::setValue);
    }
    public void deleteShopMember(String userId)
    {
        mData.addSource(mRepository.deleteRestaurantMember(getShopPk(),userId), mData::setValue);
    }


    public LiveData<Resource<List<MemberModel>>> getShopMembers() {
        return mShopMembers;
    }
    public LiveData<Resource<ObjectNode>> getShopMemberLiveData()
    {
        return mData;
    }



    @Override
    public void updateResults() {

    }
}
