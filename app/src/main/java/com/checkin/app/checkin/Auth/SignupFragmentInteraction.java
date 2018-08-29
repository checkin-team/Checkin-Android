package com.checkin.app.checkin.Auth;

import com.checkin.app.checkin.Auth.SignupUserInfoFragment.GENDER;

public interface SignupFragmentInteraction {
    void onPhoneNumberProcess(String phoneNo);
    void onOtpVerificationProcess(String otp);
    void onUserInfoProcess(String firstName, String lastName, String password, GENDER gender);
    void onSigninClicked();
}