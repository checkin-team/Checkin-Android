package com.checkin.app.checkin.Home;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.Auth.AuthActivity;
import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Utility.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            startService(new Intent(getApplicationContext(), DeviceTokenService.class));
        }

        if (AccountManager.get(getApplicationContext()).getAccountsByType(Constants.ACCOUNT_TYPE).length > 0) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }
}
