package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Profile.ShopProfile.FragmentShopMenu;
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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_root);
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

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
        return R.menu.main_navigation_menu;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new  AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected ACCOUNT_TYPE[] getAccountTypes() {
        return new ACCOUNT_TYPE[] {ACCOUNT_TYPE.SHOP_OWNER, ACCOUNT_TYPE.SHOP_ADMIN};
    }

    private class ShopPagerAdapter extends FragmentPagerAdapter {

        public ShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopProfileFragment.newInstance();
                case 1:
                    return FragmentShopMenu.newInstance("1", "2");
                case 2:
                    return FragmentShopMenu.newInstance("1", "2");
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
