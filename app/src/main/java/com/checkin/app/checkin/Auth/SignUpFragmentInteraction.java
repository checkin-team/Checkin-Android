package com.checkin.app.checkin.Auth;


import com.checkin.app.checkin.User.UserModel.GENDER;

public interface SignUpFragmentInteraction {
    void onPhoneNumberProcess(String phoneNo);
    void onResendOtpRequest();
    void onOtpVerificationProcess(String otp);
    void onUserInfoProcess(String firstName, String lastName, String username, GENDER gender);
    void onSignInClicked();
}