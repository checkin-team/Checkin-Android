package com.checkin.app.checkin.Shop.Private;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Shop.ShopRepository;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class MemberViewModel extends BaseViewModel {
    private ShopRepository mRepository;

    private SourceMappedLiveData<Resource<List<MemberModel>>> mShopMembers = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mRemovedMember = createNetworkLiveData();

    private long mShopPk;
    @Nullable
    private MemberModel mCurrentMember;

    public MemberViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.Companion.getInstance(application);
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
        getMData().addSource(mRepository.addRestaurantMember(mShopPk, mCurrentMember), getMData()::setValue);
    }

    public void updateShopMember(CharSequence[] roles) {
        if (mCurrentMember == null)
            return;
        mCurrentMember.assignRoles(roles);
        getMData().addSource(mRepository.updateRestaurantMember(mShopPk, mCurrentMember), getMData()::setValue);
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
        fetchShopMembers(mShopPk);
    }
}
