package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopBaseActivity;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopActivity extends ShopBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_private);
        setupPagers(new ShopPagerAdapter(getSupportFragmentManager()), R.menu.menu_shop_profile_private);
    }

    @Override
    protected int isDrawerEnabled(int position) {
        return position == 0 ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
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
