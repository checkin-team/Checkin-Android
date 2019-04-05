package com.checkin.app.checkin.Auth;

import com.checkin.app.checkin.User.UserModel.GENDER;
import com.facebook.login.LoginResult;

public interface AuthFragmentInteraction {
    void onUserInfoProcess(String firstName, String lastName, String username, GENDER gender);

    void onGoogleAuth();

    void onFacebookAuth(LoginResult loginResult);

    void onPhoneAuth(String phoneNo);
}