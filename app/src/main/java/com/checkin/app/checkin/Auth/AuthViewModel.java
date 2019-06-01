package com.checkin.app.checkin.Auth;

import android.app.Application;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthViewModel extends BaseViewModel {
    private static final String TAG = AuthViewModel.class.getSimpleName();

    private final AuthRepository mRepository;

    private MutableLiveData<JsonNode> mErrors = new MutableLiveData<>();
    private MediatorLiveData<Resource<AuthResultModel>> mAuthResult = new MediatorLiveData<>();

    private String firebaseIdToken;
    private String providerIdToken;
    private boolean isLoginAttempt;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mRepository = AuthRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
    }

    @Override
    protected void registerProblemHandlers() {
        mAuthResult = registerProblemHandler(mAuthResult);
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
        isLoginAttempt = true;
        mAuthResult.addSource(mRepository.login(data), mAuthResult::setValue);
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
        isLoginAttempt = false;
        mAuthResult.addSource(mRepository.register(data), mAuthResult::setValue);
    }

    public boolean isLoginAttempt() {
        return isLoginAttempt;
    }

    public MediatorLiveData<Resource<AuthResultModel>> getAuthResult() {
        return mAuthResult;
    }

    public void showError(JsonNode data) {
        mErrors.setValue(data);
    }

    public LiveData<JsonNode> getErrors() {
        return mErrors;
    }
}
