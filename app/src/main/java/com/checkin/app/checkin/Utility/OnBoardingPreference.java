package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class OnBoardingPreference {

    private static final String SP_ONBOARDING_TABLE_NAME = "onboarding";

    public static void writeOnBoardingPreference(Context context, String... onBoardingKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_ONBOARDING_TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : onBoardingKey) {
            editor.putBoolean(key, false)
                    .apply();
        }
    }

    public static boolean readOnBoardingPreference(Context context, String... onBoardingKey) {
        boolean isFirstTime = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_ONBOARDING_TABLE_NAME, Context.MODE_PRIVATE);
        for (String key : onBoardingKey){
            isFirstTime = sharedPreferences.getBoolean(key,true);
        }

        return isFirstTime;
    }
}
