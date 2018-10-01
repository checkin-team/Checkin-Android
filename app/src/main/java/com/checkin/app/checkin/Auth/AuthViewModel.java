package com.checkin.app.checkin.Auth;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AuthViewModel extends BaseViewModel{
    private static final String TAG = AuthViewModel.class.getSimpleName();

    private final AuthRepository mRepository;
    private MutableLiveData<Long> mOtpTimeOut = new MutableLiveData<>();
    private String phoneNo;
    private String firebaseIdToken;
    private String providerIdToken;

    AuthViewModel(@NonNull Application application) {
        super(application);
        mRepository = AuthRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public void setOtpTimeout(long timeout) {
        mOtpTimeOut.setValue(timeout);
        if (timeout > 0) {
            new CountDownTimer(timeout, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mOtpTimeOut.setValue(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    mOtpTimeOut.setValue(0L);
                }
            }.start();
        }
    }

    public LiveData<Long> getOtpTimeOut() {
        return mOtpTimeOut;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setFireBaseIdToken(String idToken) {
        firebaseIdToken = idToken;
    }

    public void setProviderIdToken(String idToken) {
        providerIdToken = idToken;
    }

    public void login() {
        if (firebaseIdToken == null)
            Log.e(TAG, "FireBase ID Token is NULL");
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("id_token", firebaseIdToken);
        if (providerIdToken != null)
            data.put("provider_token", providerIdToken);
        mData.addSource(mRepository.login(data), mData::setValue);
    }

    public void register(@NonNull String firstName, @Nullable String lastName, @NonNull UserModel.GENDER gender, @NonNull String username) {
        if (firebaseIdToken == null)
            Log.e(TAG, "FireBase ID Token is NULL");
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("username", username);
        data.put("id_token", firebaseIdToken);
        if (providerIdToken != null)
            data.put("provider_token", providerIdToken);
        data.put("first_name", firstName);
        data.put("gender", gender == UserModel.GENDER.MALE ? "m" : "f");
        data.put("last_name", lastName == null ? "" : lastName);
        mData.addSource(mRepository.register(data), mData::setValue);
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
