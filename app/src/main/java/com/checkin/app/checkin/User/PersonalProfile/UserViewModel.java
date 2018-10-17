package com.checkin.app.checkin.User.PersonalProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;
    private MediatorLiveData<Resource<UserModel>> mUser = new MediatorLiveData<>();

    UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getUser();
    }

    public LiveData<Resource<UserModel>> getUser() {
        mUser.addSource(mRepository.getUser(0), mUser::setValue);
        return mUser;
    }

    public void updateProfilePic(File pictureFile) {
        mData.addSource(mRepository.postUserProfilePic(pictureFile), mData::setValue);
    }

    public void postUserData(String name,String location, String bio) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("full_name", name);
        data.put("address", location);
        data.put("bio", bio);
        mData.addSource(mRepository.postUserData(data), mData::setValue);
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
