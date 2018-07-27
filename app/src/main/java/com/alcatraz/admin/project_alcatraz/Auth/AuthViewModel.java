package com.alcatraz.admin.project_alcatraz.Auth;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.Resource;

import java.util.HashMap;
import java.util.Map;


public class AuthViewModel extends AndroidViewModel{
    private final AuthRepository mRepository;
    private MediatorLiveData<Resource<Map<String, String>>> mData = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mRepository = AuthRepository.getInstance(application);
    }

    public LiveData<Resource<Map<String, String>>> getObservableData() {
        return mData;
    }

    public void login(String username, String password) {
        Map<String, String> input = new HashMap<>(2);
        input.put("username", username);
        input.put("password", password);
        mData.addSource(mRepository.login(input), mData::setValue);
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
