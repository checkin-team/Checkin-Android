package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.R;

import butterknife.ButterKnife;

public class ShopLogoEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shop_personal_profile_edit);
        ButterKnife.bind(this);
    }
}
