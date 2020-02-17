package com.checkin.app.checkin.Shop.ShopJoin;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException;
import com.checkin.app.checkin.auth.fragments.OtpVerificationDialog;
import com.checkin.app.checkin.misc.views.DynamicSwipableViewPager;
import com.checkin.app.checkin.utility.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.PhoneAuthCredential;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactVerifyActivity extends AppCompatActivity implements TabPhoneFragment.PhoneInteraction, TabEmailFragment.EmailInteraction, OtpVerificationDialog.AuthCallback {
    private static final String TAG = ContactVerifyActivity.class.getSimpleName();
    @BindView(R.id.tabs)
    TabLayout vTabs;
    @BindView(R.id.pager)
    DynamicSwipableViewPager vPager;

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_contact_verify);
        ButterKnife.bind(this);

        vPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        vPager.setEnabled(false);
        vTabs.setupWithViewPager(vPager);
        for (View v : vTabs.getTouchables()) {
            v.setClickable(false);
        }
    }

    @Override
    public void onPhoneEntered(String phone) {
        OtpVerificationDialog dialog = OtpVerificationDialog.Builder.with(this)
                .setAuthCallback(this)
                .build();
        dialog.verifyPhoneNumber(phone);
        dialog.show();
    }

    @Override
    public void onEmailEntered(String email) {
        mEmail = email;
        vPager.setCurrentItem(1, true);
    }

    @Override
    public void onSuccessVerification(DialogInterface dialog, @NotNull PhoneAuthCredential credential, @NotNull String idToken) {
        Intent intent = new Intent(getApplicationContext(), ShopJoinActivity.class);
        intent.putExtra(ShopJoinActivity.KEY_SHOP_PHONE_TOKEN, idToken);
        intent.putExtra(ShopJoinActivity.KEY_SHOP_EMAIL, mEmail);
        startActivity(intent);
        dialog.dismiss();
    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {

    }

    @Override
    public void onFailedVerification(DialogInterface dialog, @NotNull Exception exception) {
        Utils.toast(this, exception.getLocalizedMessage());
        if (!(exception instanceof InvalidOTPException)) dialog.dismiss();
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
                    fragment = TabEmailFragment.newInstance(ContactVerifyActivity.this);
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
