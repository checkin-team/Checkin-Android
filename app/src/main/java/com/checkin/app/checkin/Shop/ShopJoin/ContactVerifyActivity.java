package com.checkin.app.checkin.Shop.ShopJoin;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.AuthFragmentInteraction;
import com.checkin.app.checkin.Auth.AuthViewModel;
import com.checkin.app.checkin.Auth.OtpVerificationFragment;
import com.checkin.app.checkin.Auth.PhoneAuth;
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

public class ContactVerifyActivity extends AppCompatActivity implements TabPhoneFragment.PhoneInteraction, AuthFragmentInteraction {
    private static final String TAG = ContactVerifyActivity.class.getSimpleName();
    @BindView(R.id.tabs) TabLayout vTabs;
    @BindView(R.id.pager) ViewPager vPager;
    @BindView(R.id.dark_back) ViewGroup mDarkBack;

    private AuthViewModel mAuthViewModel;
    private FirebaseAuth mAuth;
    private PhoneAuth mPhoneAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_contact_verify);
        ButterKnife.bind(this);

        vPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        vTabs.setupWithViewPager(vPager);

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        mPhoneAuth = new PhoneAuth(mAuth) {
            @Override
            protected void onVerificationSuccess(PhoneAuthCredential credential) {
                successVerification(credential);
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

    private void successVerification(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(result -> {
                    Intent intent = new Intent(getApplicationContext(), ShopJoinActivity.class);
                    intent.putExtra(ShopJoinActivity.KEY_SHOP_PHONE_TOKEN, result.getToken());
                    startActivity(intent);
                });
            } else {
                Log.e(TAG, "Authentication failed", task.getException());
                Toast.makeText(getApplicationContext(),R.string.error_authentication,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPhoneEntered(String phone) {
        mAuthViewModel.setPhoneNo(phone);
        verifyPhoneNumber();
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance(this);
        otpVerificationFragment.setEnterTransition(new Fade().setDuration(5));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, otpVerificationFragment)
                .addToBackStack(null)
                .commit();
        showDarkBack();
    }

    private void verifyPhoneNumber() {
        mAuthViewModel.setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNo(mAuthViewModel.getPhoneNo(), this);
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
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
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
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    @Override
    public void onResendOtpRequest() {
        verifyPhoneNumber();
    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        successVerification(mPhoneAuth.verifyOtp(otp));
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String username, UserModel.GENDER gender) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onGoogleAuth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onFacebookAuth(LoginResult loginResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onPhoneAuth(String phoneNo) {
        throw new UnsupportedOperationException();
    }

    private class TabAdapter extends FragmentStatePagerAdapter {

        TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TabEmailFragment.newInstance();
                    break;
                case 1:
                    fragment = TabPhoneFragment.newInstance(ContactVerifyActivity.this);
                    break;
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "Email";
                    break;
                case 1:
                    title = "Phone";
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
