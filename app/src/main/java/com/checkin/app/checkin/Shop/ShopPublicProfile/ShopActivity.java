package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopBaseActivity;

public class ShopActivity extends ShopBaseActivity {
    private static final String TAG = ShopActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_public);
        setupPagers(new ShopPagerAdapter(getSupportFragmentManager()), R.menu.menu_shop_profile_public);
    }

    private class ShopPagerAdapter extends FragmentPagerAdapter {

        public ShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopProfileFragment.getInstance(1);
                case 1:
                    return ShopProfileFragment.getInstance(1);
                case 2:
                    return ShopProfileFragment.getInstance(1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
