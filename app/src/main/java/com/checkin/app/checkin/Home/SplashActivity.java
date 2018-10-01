package com.checkin.app.checkin.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.Auth.AuthActivity;
import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Utility.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().post(() -> ApiClient.getApiService(getApplicationContext()));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(Constants.SP_LOGGED_IN, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }
}
