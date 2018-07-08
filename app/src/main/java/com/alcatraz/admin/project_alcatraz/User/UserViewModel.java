package com.alcatraz.admin.project_alcatraz.User;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private LiveData<List<User>> mAllUsers;

    public UserViewModel(@NonNull Application application, UserRepository userRepository) {
        super(application);
        mRepository = userRepository;
    }

    public LiveData<List<User>> getAllUsers() {
        if (mAllUsers == null)
            mAllUsers = mRepository.getAllUsers();
        return mAllUsers;
    }

    public void createUsers(User... users) {
        mRepository.insertUsers(users);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;
        private final UserRepository mRepository;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mRepository = UserRepository.getInstance(application.getApplicationContext());
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserViewModel(mApplication, mRepository);
        }
    }
}
