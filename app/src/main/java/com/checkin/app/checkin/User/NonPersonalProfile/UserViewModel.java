package com.checkin.app.checkin.User.NonPersonalProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;

import java.util.List;

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;
    private long userPk;
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

    public LiveData<Resource<UserModel>> getUser() {
        mUser.addSource(mRepository.getUser(userPk), mUser::setValue);
        return mUser;
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
