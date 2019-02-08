package com.checkin.app.checkin.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Utility.Constants;

public class AuthPreferences {
    public static String getAuthToken(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accountManager.peekAuthToken(accounts[0], AccountManager.KEY_AUTHTOKEN);
        }
        return null;
    }

    @Nullable
    public static Account getCurrentAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts.length == 1) {
            return accounts[0];
        }
        return null;
    }

    public static boolean removeCurrentAccount(Context context) {
        Account account = getCurrentAccount(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return AccountManager.get(context).removeAccountExplicitly(account);
        }
        return false;
    }
}
