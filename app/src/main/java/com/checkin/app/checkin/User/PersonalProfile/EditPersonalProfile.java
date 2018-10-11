package com.checkin.app.checkin.User.PersonalProfile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.AuthFragmentInteraction;
import com.checkin.app.checkin.Auth.AuthOptionsFragment;
import com.checkin.app.checkin.Auth.AuthViewModel;
import com.checkin.app.checkin.Auth.OtpVerificationFragment;
import com.checkin.app.checkin.Auth.PhoneAuth;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Util;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.R.string.error_unavailable_network;
import static java.security.AccessController.getContext;

public class EditPersonalProfile extends AppCompatActivity implements AuthFragmentInteraction {

    @BindView(R.id.btn_back)Button btnBack;
    @BindView(R.id.btn_done)Button btnDone;
    @BindView(R.id.et_name)EditText etName;
    @BindView(R.id.et_location)EditText etLocation;
    @BindView(R.id.et_bio)EditText etBio;
    @BindView(R.id.et_phoneNumber)EditText etPhone;
    @BindView(R.id.et_email)EditText etEmail;
    @BindView(R.id.dark_back) FrameLayout mDarkBack;
    private PhoneAuth mPhoneAuth;
    private FirebaseAuth mAuth;
    private AuthViewModel mAuthViewModel;
    UserViewModel mUserViewModel;
    private AuthFragmentInteraction mInteractionListener;
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    Fragment phoneChangeFragment=AuthOptionsFragment.newInstance(this, null);
    private String TAG="TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_profile);
        ButterKnife.bind(this);

        mDarkBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getSupportFragmentManager().popBackStack();
                hideDarkBack();
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                return true;
            }
        });

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mUserViewModel.getCurrentUser().observe(this, userModelResource -> {
            if (userModelResource != null && userModelResource.status == Resource.Status.SUCCESS) {
                setUi(userModelResource.data);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mPhoneAuth = new PhoneAuth(mAuth) {
            @Override
            protected void onVerificationSuccess(PhoneAuthCredential credential) {
                mUserViewModel.postPhoneNumber(mAuthViewModel.getPhoneNo());
            }

            @Override
            protected void onVerificationError(FirebaseException e) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e);
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(getApplicationContext(), R.string.error_unavailable_network, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_authentication_phone, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onOtpRetrievalTimedOut() {
                mAuthViewModel.setOtpTimeout(0L);
            }
        };

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);
    }

    @Override
    public void onResendOtpRequest() {
        verifyPhoneNumber();
    }

    @Override
    public void onOtpVerificationProcess(String otp) {

    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String username, UserModel.GENDER gender) {}

    @Override
    public void onGoogleAuth() {}

    @Override
    public void onFacebookAuth(LoginResult loginResult) {}

    @Override
    public void onPhoneAuth(String phoneNo) {
        Log.e(TAG, "Phone number: " + phoneNo);
        mAuthViewModel.setPhoneNo(phoneNo);
        verifyPhoneNumber();
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance(this);
        otpVerificationFragment.setEnterTransition(new Fade().setDuration(5));
        replaceFragmentContainer(otpVerificationFragment);
    }

    private void verifyPhoneNumber() {
        mAuthViewModel.setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNo(mAuthViewModel.getPhoneNo(), this);
    }


    private void replaceFragmentContainer(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showDarkBack() {
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideDarkBack() {
        mDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.btn_done)
    public void updatingInfo()
    {
        mUserViewModel.postUserData(etName.toString(),etLocation.toString(),etBio.toString());
    }

    @OnClick(R.id.et_phoneNumber)
    public void changePhoneNumber()
    {
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        transaction.add(R.id.fragment_container,phoneChangeFragment );
        transaction.addToBackStack(null);
        transaction.commit();
        showDarkBack();
    }

    @OnClick(R.id.et_email)
    public void changeEmailId()
    {

    }

    private void setUi(UserModel mUserModel) {
        etName.setText(mUserModel.getUsername());
        etLocation.setText(mUserModel.getAddress());
        etBio.setText(mUserModel.getBio());
        //etPhone.setText(mUserModel.getPhoneNumber);
        //etEmail.setText(mUserModel.getEmail);

    }
    @OnClick(R.id.btn_back)
    public void goBack()
    {
        finish();
    }

}
