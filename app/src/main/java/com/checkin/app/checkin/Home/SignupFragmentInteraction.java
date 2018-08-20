package com.checkin.app.checkin.Home;

import com.checkin.app.checkin.Home.SignupUserInfoFragment.GENDER;

public interface SignupFragmentInteraction {
    void onPhoneNumberProcess(String phoneNo);
    void onOtpVerificationProcess(String otp);
    void onUserInfoProcess(String firstName, String lastName, String password, GENDER gender);
    void onSigninClicked();
}