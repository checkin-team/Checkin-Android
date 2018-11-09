package com.checkin.app.checkin.User.PersonalProfile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.transition.Fade;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.AuthFragmentInteraction;
import com.checkin.app.checkin.Auth.AuthViewModel;
import com.checkin.app.checkin.Auth.OtpVerificationFragment;
import com.checkin.app.checkin.Auth.PhoneAuth;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Constants;
import com.facebook.login.LoginResult;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileEditActivity extends AppCompatActivity implements AuthFragmentInteraction {
    private static final String TAG = ProfileEditActivity.class.getSimpleName();

    @BindView(R.id.et_first_name) EditText etFirstName;
    @BindView(R.id.et_last_name) EditText etLastName;
    @BindView(R.id.et_location) EditText etLocation;
    @BindView(R.id.et_bio) EditText etBio;
    @BindView(R.id.et_phone) EditText etPhone;
    @BindView(R.id.tv_username) TextView tvUsername;
    @BindView(R.id.dark_back) FrameLayout mDarkBack;

    private PhoneAuth mPhoneAuth;
    private AuthViewModel mAuthViewModel;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        ButterKnife.bind(this);

//        mDarkBack.setOnTouchListener((v, event) -> {
//            getSupportFragmentManager().popBackStack();
//            hideDarkBack();
//            findViewById(R.id.fragment_container).setVisibility(View.GONE);
//            return true;
//        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);

        mUserViewModel.getUser().observe(this, userModelResource -> {
            if (userModelResource.status == Resource.Status.SUCCESS && userModelResource.data != null) {
                setUi(userModelResource.data);
            }
        });
        mUserViewModel.getObservableData().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mPhoneAuth = new PhoneAuth(mAuth) {
            @Override
            protected void onVerificationSuccess(PhoneAuthCredential credential) {
//                mUserViewModel.postPhoneNumber(mAuthViewModel.getPhoneNo());
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

    }

    @Override
    public void onResendOtpRequest() {
        verifyPhoneNumber();
    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        mPhoneAuth.verifyOtp(otp);
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

    @OnClick(R.id.et_phone)
    public void changePhoneNumber()
    {
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
//        transaction.add(R.id.fragment_container,phoneChangeFragment );
//        transaction.addToBackStack(null);
//        transaction.commit();
        showDarkBack();
    }


    private void setUi(UserModel user) {
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etLocation.setText(user.getAddress());
        etBio.setText(user.getBio());
        etPhone.setText(user.getPhoneNumber());
        tvUsername.setText("Username: " + user.getUsername());
        //etEmail.setText(mUserModel.getEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done: {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String location = etLocation.getText().toString();
                String bio = etBio.getText().toString();
                mUserViewModel.postUserData(firstName, lastName, location, bio);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
