package com.checkin.app.checkin.User.Public;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.Friendship.FriendshipRequestModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;

    private long mUserPk;
    private String requestPk;
    private MediatorLiveData<Resource<UserModel>> mUser = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ShopCustomerModel>>> mUserCheckinsData = new MediatorLiveData<>();
    private LiveData<Resource<List<UserModel>>> mAllUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getUser();
    }

    public LiveData<Resource<List<UserModel>>> getAllUsers() {
        if (mAllUsers == null)
            mAllUsers = mRepository.getAllUsers();
        return mAllUsers;
    }

    public void setUserPk(long mUserPk) {
        this.mUserPk = mUserPk;
    }

    public void removeFriend() {
        mData.addSource(mRepository.removeFriend(mUserPk), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void addFriend(String message) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("to_user", mUserPk);
        data.put("message", message);
        mData.addSource(mRepository.addFriend(data), mData::setValue);
    }

    public LiveData<Resource<UserModel>> getUser() {
        mUser.addSource(mRepository.getUser(mUserPk), value -> {
            if (value.status == Resource.Status.SUCCESS && value.data != null) {
                FriendshipRequestModel request = value.data.getFriendshipRequest();
                requestPk = request != null ? request.getPk() : null;
            }
            mUser.setValue(value);
        });
        return mUser;
    }

    public void acceptFriendRequest() {
        mData.addSource(mRepository.acceptFriendRequest(requestPk), mData::setValue);
    }

    public void cancelFriendRequest() {
        mData.addSource(mRepository.cancelFriendRequest(requestPk), mData::setValue);
    }

    public void rejectFriendRequest() {
        mData.addSource(mRepository.rejectFriendRequest(requestPk), mData::setValue);
    }

    public long getPk() {
        return mUserPk;
    }

    public void fetchUserCheckins(long userId) {
        mUserCheckinsData.addSource(mRepository.getUserCheckinById(userId), mUserCheckinsData::setValue);
    }

    public LiveData<Resource<List<ShopCustomerModel>>> getUserCheckinsData(){
        return mUserCheckinsData;
    }
}
