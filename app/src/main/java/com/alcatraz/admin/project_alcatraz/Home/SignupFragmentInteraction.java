package com.alcatraz.admin.project_alcatraz.Home;

import com.alcatraz.admin.project_alcatraz.Home.SignupUserInfoFragment.GENDER;

public interface SignupFragmentInteraction {
    void onPhoneNumberProcess(String phoneNo);
    void onOtpVerificationProcess(String otp);
    void onUserInfoProcess(String firstName, String lastName, String password, GENDER gender);
    void onSigninClicked();
}