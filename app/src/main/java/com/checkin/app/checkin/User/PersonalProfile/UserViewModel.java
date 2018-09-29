package com.checkin.app.checkin.User.PersonalProfile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.Utility.Constants;

import java.io.File;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    public void postImages(File rectangleFile) {
        mRepository.postImage(rectangleFile, 1L);
    }

    public LiveData<Resource<UserModel>> getCurrentUser() {
        return mRepository.getUser(getUserId());
    }

    private long getUserId() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getLong(Constants.SP_USER_ID,1L);
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
