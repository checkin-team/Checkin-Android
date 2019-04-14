package com.checkin.app.checkin.Shop.Private;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.InventoryActivity;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.Misc.BlankFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopPrivateActivity extends BaseAccountActivity implements ShopPrivateProfileFragment.onSwipeGesture {

    public static final String KEY_SHOP_PK = "shop_private.pk";
    @BindView(R.id.vp_shop_private)
    DynamicSwipableViewPager vpShopPrivate;
    @BindView(R.id.drawer_shop_private)
    DrawerLayout drawerRoot;
    @BindView(R.id.tabs_shop_private)
    TabLayout tabsShopPrivate;
    @BindView(R.id.iv_shop_profile_navigation)
    ImageView ivShopProfileNavigation;
    @BindView(R.id.sr_shop_private)
    SwipeRefreshLayout swipeRefreshLayout;
    private ShopProfileViewModel mViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_private);
        ButterKnife.bind(this);
        initRefreshScreen(R.id.sr_shop_private);

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopDetails(shopPk);

        mViewModel.getShopData().observe(this, restaurantModelResource -> {
            if (restaurantModelResource == null)
                return;
            if (restaurantModelResource.status == Resource.Status.SUCCESS && restaurantModelResource.data != null) {
                stopRefreshing();
            } else if (restaurantModelResource.status == Resource.Status.LOADING) {
                startRefreshing();
            } else {
                stopRefreshing();
                Utils.toast(this, restaurantModelResource.message);
            }
        });

        setup();
    }

    private void setup() {
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerRoot, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerRoot.addDrawerListener(startToggle);
        startToggle.syncState();

        ShopFragmentAdapter adapter = new ShopFragmentAdapter(getSupportFragmentManager(), this);
        vpShopPrivate.setEnabled(false);
        vpShopPrivate.setAdapter(adapter);
        tabsShopPrivate.setupWithViewPager(vpShopPrivate);
        adapter.setupWithTab(tabsShopPrivate, vpShopPrivate);
        vpShopPrivate.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    launchMenu();
                    vpShopPrivate.setCurrentItem(0);
                }
                super.onPageSelected(position);
            }
        });
    }

    @Override
    protected int getDrawerRootId() {
        return R.id.drawer_shop_private;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.drawer_shop_admin;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.SHOP_OWNER, AccountModel.ACCOUNT_TYPE.SHOP_ADMIN};
    }

    @OnClick(R.id.iv_shop_profile_navigation)
    public void onViewClicked() {
        drawerRoot.openDrawer(GravityCompat.START);
    }

    private void launchMenu() {
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.putExtra(InventoryActivity.KEY_INVENTORY_RESTAURANT_PK, mViewModel.getShopPk());
        startActivity(intent);
    }

    @Override
    protected void updateScreen() {
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
    }

    @Override
    public void onSwipeLeft() {
        swipeRefreshLayout.setRefreshing(false);
                Log.e("swipe==", "inside====");
    }

    @Override
    public void onSwipeRight() {
        swipeRefreshLayout.setRefreshing(false);
//        Log.e("swipe==", "inside==== ri");

    }

    private class ShopFragmentAdapter extends BaseFragmentAdapterBottomNav {
        ShopPrivateProfileFragment.onSwipeGesture mSwipeGesture;
        ShopFragmentAdapter(FragmentManager fm, ShopPrivateProfileFragment.onSwipeGesture swipeGesture) {
            super(fm);
            mSwipeGesture = swipeGesture;
        }

        @Override
        public int getTabDrawable(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_tab_home_toggle;
                case 1:
                    return R.drawable.ic_tab_menu_toggle;
                default:
                    return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopPrivateProfileFragment.newInstance(mSwipeGesture);
                case 1:
                    return BlankFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        protected void onTabClick(int position) {
            if (position == 1) launchMenu();
            else super.onTabClick(position);
        }
    }
}
