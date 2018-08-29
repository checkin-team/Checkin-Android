package com.checkin.app.checkin.Shop;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.checkin.app.checkin.R;

public class SignUpVerifyActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private static final String TAG = "SignUpIntroActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile_after_create_account);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_appbar);
        actionBar.setElevation(20);
        actionBar.setTitle("");
        Log.e(TAG, "onCreate: "+actionBar );


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabEmailFragment(), "Email");
        adapter.addFragment(new TabPhoneFragment(), "Phone");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
