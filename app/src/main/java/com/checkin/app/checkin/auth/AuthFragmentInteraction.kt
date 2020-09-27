package com.checkin.app.checkin.auth

import com.facebook.login.LoginResult

interface AuthFragmentInteraction {
    fun onGoogleAuth()
    fun onFacebookAuth(loginResult: LoginResult)
    fun onPhoneAuth(phoneNo: String)
}