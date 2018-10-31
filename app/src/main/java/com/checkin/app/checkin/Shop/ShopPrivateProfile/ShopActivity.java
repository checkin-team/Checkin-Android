package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopActivity extends BaseAccountActivity {
    private static final String TAG = "ShopActivityPrivate";

    @BindView(R.id.view_pager)
    protected DynamicSwipableViewPager mViewPager;
    @BindView(R.id.bottom_navigation)
    protected BottomNavigation mBottomNavigation;

    public static final String KEY_SHOP_PK = "shop_private.pk";

    private ShopProfileViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_private);
        ButterKnife.bind(this);


        setupPagers(new ShopPagerAdapter(getSupportFragmentManager()), R.menu.menu_shop_profile_private);

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        String shopPk = getIntent().getStringExtra(KEY_SHOP_PK);
        mViewModel.fetchShop(shopPk);
    }

    protected void setupPagers(FragmentPagerAdapter pagerAdapter, @MenuRes int menuId) {
        mViewPager.setEnabled(false);
        mViewPager.setAdapter(pagerAdapter);
        mBottomNavigation.inflateMenu(menuId);
        mBottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes final int itemId, final int position, final boolean fromUser) {
                mViewPager.setCurrentItem(position, true);
            }

            @Override
            public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {
            }
        });
    }

    @Override
    protected int getNavMenu() {
        return 0;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return null;
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE getAccountType() {
        return null;
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
