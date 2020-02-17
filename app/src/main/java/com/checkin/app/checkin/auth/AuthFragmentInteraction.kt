package com.checkin.app.checkin.auth

import com.checkin.app.checkin.user.models.UserModel.GENDER
import com.facebook.login.LoginResult

interface AuthFragmentInteraction {
    fun onUserInfoProcess(firstName: String, lastName: String, username: String, gender: GENDER)
    fun onGoogleAuth()
    fun onFacebookAuth(loginResult: LoginResult)
    fun onPhoneAuth(phoneNo: String)
}