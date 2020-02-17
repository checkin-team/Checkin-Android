package com.checkin.app.checkin.auth.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService : Service() {
    private lateinit var mAuthenticator: AccountAuthenticator

    override fun onCreate() {
        mAuthenticator = AccountAuthenticator(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator.iBinder
    }
}