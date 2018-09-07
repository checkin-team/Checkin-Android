package com.checkin.app.checkin.Shop;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpVerifyActivity extends AppCompatActivity {
    private static final String TAG = SignUpVerifyActivity.class.getSimpleName();
    @BindView(R.id.tabs) TabLayout vTabs;
    @BindView(R.id.pager) ViewPager vPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verify);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);
        actionBar.setTitle("");

        vPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        vTabs.setupWithViewPager(vPager);
    }

    private static class TabAdapter extends FragmentStatePagerAdapter {

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
                    fragment = TabPhoneFragment.newInstance();
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
