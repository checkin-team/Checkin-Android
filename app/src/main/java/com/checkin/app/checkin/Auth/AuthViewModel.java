package com.checkin.app.checkin.Auth;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class AuthViewModel extends BaseViewModel{
    private final AuthRepository mRepository;

    AuthViewModel(@NonNull Application application) {
        super(application);
        mRepository = AuthRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public void login(String username, String password) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("username", username);
        data.put("password", password);
        mData.addSource(mRepository.login(data), mData::setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AuthViewModel(mApplication);
        }
    }
}
