package com.checkin.app.checkin.Home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.User.UserViewModel;

import java.util.List;

public class UserProfileViewModel  extends AndroidViewModel {
    private UserProfileRepository mRepository;
    private LiveData<Resource<List<UserProfileEntity>>> mAllUsers;

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserProfileRepository.getInstance(application);
    }

    public LiveData<Resource<List<UserProfileEntity>>> getAllUsers(long id) {
        if (mAllUsers == null)
            mAllUsers = mRepository.getLiveData(id);
        return mAllUsers;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserProfileViewModel(mApplication);
        }
    }
}
