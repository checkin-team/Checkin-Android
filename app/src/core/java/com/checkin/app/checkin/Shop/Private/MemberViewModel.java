package com.checkin.app.checkin.Shop.Private;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.ShopRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class MemberViewModel extends BaseViewModel {
    private ShopRepository mRepository;
    private MediatorLiveData<Resource<List<MemberModel>>> mShopMembers = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ObjectNode>> mRemovedMember = new MediatorLiveData<>();

    private long mShopPk;
    @Nullable private MemberModel mCurrentMember;

    public MemberViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
    }

    public void fetchShopMembers(long shopPk) {
        mShopPk = shopPk;
        mShopMembers.addSource(mRepository.getRestaurantMembers(mShopPk), mShopMembers::setValue);
    }

    public long getShopPk() {
        return mShopPk;
    }

    public void addShopMember(CharSequence[] roles) {
        if (mCurrentMember == null)
            return;
        mCurrentMember.assignRoles(roles);
        mData.addSource(mRepository.addRestaurantMember(mShopPk, mCurrentMember), mData::setValue);
    }

    public void updateShopMember(CharSequence[] roles) {
        if (mCurrentMember == null)
            return;
        mCurrentMember.assignRoles(roles);
        mData.addSource(mRepository.updateRestaurantMember(mShopPk, mCurrentMember), mData::setValue);
    }

    public void deleteShopMember() {
        if (mCurrentMember == null)
            return;
        long userId = mCurrentMember.getUserId();
        mRemovedMember.addSource(mRepository.removeRestaurantMember(mShopPk, userId), mRemovedMember::setValue);
    }

    public LiveData<Resource<ObjectNode>> getRemovedMemberData() {
        return mRemovedMember;
    }

    public void resetRemovedMemberData() {
        mRemovedMember.setValue(null);
    }

    public void resetData() {
        resetObservableData();
        resetRemovedMemberData();
    }

    @Nullable
    public MemberModel getCurrentMember() {
        return mCurrentMember;
    }

    public void setCurrentMember(MemberModel member) {
        mCurrentMember = member;
    }

    public LiveData<Resource<List<MemberModel>>> getShopMembers() {
        return mShopMembers;
    }

    @Override
    public void updateResults() {

    }
}
