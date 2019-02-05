package com.checkin.app.checkin.Home;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Auth.AuthActivity;
import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Manager.ManagerWorkActivity;
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Waiter.WaiterWorkActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!prefs.getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            startService(new Intent(getApplicationContext(), DeviceTokenService.class));
        }

        if (AccountManager.get(getApplicationContext()).getAccountsByType(Constants.ACCOUNT_TYPE).length == 0) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }

        int accountTag = prefs.getInt(Constants.SP_LAST_ACCOUNT_TYPE, 201);
        long accountPk = prefs.getLong(Constants.SP_LAST_ACCOUNT_PK, 0);
        AccountModel.ACCOUNT_TYPE accountType = AccountModel.ACCOUNT_TYPE.getById(accountTag);
        Intent intent;
        switch (accountType) {
            case SHOP_OWNER:
            case SHOP_ADMIN:
                intent = new Intent(this, ShopPrivateActivity.class);
                intent.putExtra(ShopPrivateActivity.KEY_SHOP_PK, accountPk);
                break;
            case RESTAURANT_WAITER:
                intent = new Intent(this, WaiterWorkActivity.class);
                intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, accountPk);
                break;
            case RESTAURANT_MANAGER:
                intent = new Intent(this, ManagerWorkActivity.class);
                intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, accountPk);
                break;
            default:
                intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
