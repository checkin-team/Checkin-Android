package com.checkin.app.checkin.User.PrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserRepository;

import java.util.List;

public class FriendsViewModel extends BaseViewModel {

    private UserRepository mRepository;
    private MediatorLiveData<Resource<List<FriendshipModel>>> mFriends = new MediatorLiveData<>();
    private Long userPk;

    public LiveData<Resource<List<FriendshipModel>>> getUserFriends(){
        if(userPk == 0)
            mFriends.addSource(mRepository.getSelfFriends(), mFriends::setValue);
        else
            mFriends.addSource(mRepository.getUserFriends(userPk), mFriends::setValue);
        return mFriends;
    }
    public FriendsViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getUserFriends();
    }

    public void removeFriend(long userPk) {
        mData.addSource(mRepository.removeFriend(userPk), objectNodeResource -> mData.setValue(objectNodeResource));
    }

    public void postNewFriends(long userPk){
        mData.addSource(mRepository.postNewFriends(userPk), mData::setValue);
    }

    public void setUserPk(long userPk) {
        this.userPk = userPk;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FriendsViewModel(mApplication);
        }
    }
}
