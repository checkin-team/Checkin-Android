package com.checkin.app.checkin.User.NonPersonalProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
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
    private long userPk;
    private String requestPk;
    private MediatorLiveData<Resource<UserModel>> mUser = new MediatorLiveData<>();
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

    public void setUserPk(long userPk) {
        this.userPk = userPk;
    }

    public void removeFriend() {
        mData.addSource(mRepository.removeFriend(userPk), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void addFriend(String message) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("to_user", userPk);
        data.put("message", message);
        mData.addSource(mRepository.addFriend(data), mData::setValue);
    }

    public LiveData<Resource<UserModel>> getUser() {
        mUser.addSource(mRepository.getUser(userPk), value -> {
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
        return userPk;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserViewModel(mApplication);
        }
    }
}
