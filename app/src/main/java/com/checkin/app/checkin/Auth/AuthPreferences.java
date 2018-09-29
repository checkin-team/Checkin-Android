package com.checkin.app.checkin.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Utility.Constants;

import java.io.IOException;

public class AuthPreferences {

    public static void getAuthToken(Context context, @NonNull final AuthPreferencesInteraction listener) {
        AccountManagerCallback<Bundle> accountManagerCallback = future -> {
            if (future.isDone()) {
                try {
                    Bundle result = future.getResult();
                    listener.onTokenReceived(result.getString(AccountManager.KEY_AUTHTOKEN));
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }
            }
        };

        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        accountManager.getAuthToken(accounts[0], AccountManager.KEY_AUTHTOKEN, null, true, accountManagerCallback, new Handler());
    }

    public interface AuthPreferencesInteraction {
        void onTokenReceived(String token);
    }
}
