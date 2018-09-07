package com.checkin.app.checkin.Auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.checkin.app.checkin.Auth.SignupUserInfoFragment.GENDER;
import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements SignupFragmentInteraction {
    private String TAG = SignupActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    private SharedPreferences mPrefs;
    private boolean goBack = true;
    private static final int PHONE_LOGIN_REQUEST_CODE = 100;
    @BindView(R.id.dark_back) View mDarkBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, SignupPhoneFragment.newInstance(this));
            transaction.commit();
        }
    }

    private void replaceFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPhoneNumberProcess(String phoneNo) {
        // TODO: Process phone no.
        Log.e(TAG, "Phone No: " + phoneNo);
        OtpVerificationFragment otpVerificationFragment=OtpVerificationFragment.newInstance(this);
        Fade fade=new Fade();
        fade.setDuration(5);
        otpVerificationFragment.setEnterTransition(fade);
        replaceFragmentContainer(otpVerificationFragment);
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        Log.e(TAG, "OTP: " + otp);
        goBack = false;
        replaceFragmentContainer(SignupUserInfoFragment.newInstance(this));
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String password, GENDER gender) {
        Log.e(TAG, "First name: " + firstName + " | Last name: " + lastName + " | Password: " + password + " | Gender: " + gender.name());
        // TODO: Test data population
        TestDb.populateWithTestData(getApplicationContext());

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.SP_USER_ID, "1");
        editor.putBoolean(Constants.SP_LOGGED_IN, true);
        editor.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onSigninClicked() {
        startActivityForResult(new Intent(this, LoginActivity.class), PHONE_LOGIN_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            mDarkBack.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDarkBack.setVisibility(View.GONE);
                        }
                    });
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHONE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.SP_LOGGED_IN,false)){
                    finish();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
