package com.checkin.app.checkin;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class ApplicationCheckin extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
