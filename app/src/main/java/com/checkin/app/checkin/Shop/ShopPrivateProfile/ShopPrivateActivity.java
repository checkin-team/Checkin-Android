package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.widget.ImageView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopPrivateActivity extends BaseAccountActivity {

    @BindView(R.id.vp_shop_private)
    DynamicSwipableViewPager vpShopPrivate;
    @BindView(R.id.drawer_root)
    DrawerLayout drawerRoot;
    @BindView(R.id.tabs_shop_private)
    TabLayout tabsShopPrivate;
    @BindView(R.id.iv_shop_profile_Navigation)
    ImageView ivShopProfileNavigation;

    private ShopProfileViewModel mViewModel;
    public static final String KEY_SHOP_PK = "shop_private.pk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_private);
        ButterKnife.bind(this);

        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerRoot, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerRoot.addDrawerListener(startToggle);
        startToggle.syncState();

        setUpMyViewPager(new NewShopPrivatePagerAdapter(getSupportFragmentManager()));

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);

        mViewModel.fetchShopDetails(shopPk);
    }

    private void setUpMyViewPager(NewShopPrivatePagerAdapter newShopPrivatePagerAdapter) {
        vpShopPrivate.setEnabled(false);
        vpShopPrivate.setAdapter(newShopPrivatePagerAdapter);
        tabsShopPrivate.setupWithViewPager(vpShopPrivate);
        newShopPrivatePagerAdapter.setupWithTab(tabsShopPrivate, vpShopPrivate);

        vpShopPrivate.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1)
                    SessionMenuActivity.withoutSession(getApplicationContext(), mViewModel.getShopPk());
            }
        });
    }

    @Override
    protected int getNavMenu() {
        return R.menu.main_navigation_menu;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.SHOP_OWNER, AccountModel.ACCOUNT_TYPE.SHOP_ADMIN};
    }

    @OnClick(R.id.iv_shop_profile_Navigation)
    public void onViewClicked() {
        drawerRoot.openDrawer(Gravity.START);
    }

    static class NewShopPrivatePagerAdapter extends BaseFragmentAdapterBottomNav {
        NewShopPrivatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getTabDrawable(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_home_toggle;
                case 1:
                    return R.drawable.ic_food_menu_toggle;
                default:
                    return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopPrivateProfileFragment.newInstance();
                case 1:
                    return ShopPrivateProfileFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
