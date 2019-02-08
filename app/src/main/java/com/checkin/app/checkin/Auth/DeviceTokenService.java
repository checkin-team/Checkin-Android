package com.checkin.app.checkin.Auth;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceTokenService extends IntentService implements OnSuccessListener<InstanceIdResult>, Callback<ObjectNode> {
    private static final String TAG = DeviceTokenService.class.getSimpleName();

    public static final String KEY_TOKEN = "device_token";

    public DeviceTokenService() {
        super(TAG);
        Log.e(TAG, "Service started!!");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.hasExtra(KEY_TOKEN)) {
            saveToken(intent.getStringExtra(KEY_TOKEN));
        } else {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this);
        }
    }

    @Override
    public void onSuccess(InstanceIdResult instanceIdResult) {
        String token = instanceIdResult.getToken();
        saveToken(token);
    }

    private void saveToken(String token) {
        AuthRepository.getInstance(getApplication())
                .postDeviceToken(token)
                .enqueue(this);
    }

    private void processResponse(ApiResponse<ObjectNode> response) {
        if (response.isSuccessful()) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit()
                    .putBoolean(Constants.SP_SYNC_DEVICE_TOKEN, true)
                    .apply();
        }
    }

    @Override
    public void onResponse(@NonNull Call<ObjectNode> call, @NonNull Response<ObjectNode> response) {
        processResponse(new ApiResponse<>(response));
    }

    @Override
    public void onFailure(@NonNull Call<ObjectNode> call, @NonNull Throwable t) {
        processResponse(new ApiResponse<>(t));
    }
}
