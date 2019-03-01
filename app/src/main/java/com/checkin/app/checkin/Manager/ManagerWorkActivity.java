package com.checkin.app.checkin.Manager;

import android.content.Intent;
import android.os.Bundle;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Fragment.ManagerStatsFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerTablesFragment;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerWorkActivity extends BaseAccountActivity {
    private static final String TAG = ManagerWorkActivity.class.getSimpleName();

    public static final String KEY_RESTAURANT_PK = "manager.restaurant_pk";
    public static final String KEY_SESSION_BUNDLE = "manager.session_bundle";

    @BindView(R.id.pager_manager_work)
    DynamicSwipableViewPager pagerManager;
    @BindView(R.id.tabs_manager_work)
    TabLayout tabLayout;
    @BindView(R.id.drawer_manager_work)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar_manager_work)
    Toolbar toolbar;

    private ManagerWorkViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_work);
        ButterKnife.bind(this);

        initRefreshScreen(R.id.sr_manager_work);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Live Orders");
            actionBar.setElevation(getResources().getDimension(R.dimen.card_elevation));
            actionBar.setDisplayHomeAsUpEnabled(true);

            ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(startToggle);
            startToggle.syncState();
        }

        mViewModel = ViewModelProviders.of(this).get(ManagerWorkViewModel.class);
        mViewModel.fetchActiveTables(getIntent().getLongExtra(KEY_RESTAURANT_PK, 0L));

        Bundle sessionBundle = getIntent().getBundleExtra(KEY_SESSION_BUNDLE);
        if (sessionBundle != null) {
            Intent intent = new Intent(this, ManagerSessionActivity.class);
            intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, sessionBundle.getLong(ManagerSessionActivity.KEY_SESSION_PK))
                    .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk())
                    .putExtra(ManagerSessionActivity.KEY_OPEN_ORDERS, sessionBundle.getBoolean(ManagerSessionActivity.KEY_OPEN_ORDERS));
            startActivity(intent);
        }

        mViewModel.getActiveTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                stopRefreshing();
            } else if (listResource.status == Resource.Status.LOADING)
                startRefreshing();
            else {
                Utils.toast(this, listResource.message);
                stopRefreshing();
            }
        });

        pagerManager.setEnabled(false);
        ManagerFragmentAdapter pagerAdapter = new ManagerFragmentAdapter(getSupportFragmentManager());
        pagerManager.setAdapter(pagerAdapter);
        pagerManager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    if (position == 0) {
                        actionBar.setTitle("Live Orders");
                    } else if (position == 1) {
                        actionBar.setTitle("Statistics");
                    }
                }
            }
        });
        tabLayout.setupWithViewPager(pagerManager);
        pagerAdapter.setupWithTab(tabLayout, pagerManager);
    }

    @Override
    protected int getDrawerRootId() {
        return R.id.drawer_manager_work;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.drawer_manager_work;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.RESTAURANT_MANAGER};
    }

    @Override
    protected void updateScreen() {
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScreen();
    }

    static class ManagerFragmentAdapter extends BaseFragmentAdapterBottomNav {
        public ManagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getTabDrawable(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_orders_list_toggle;
                case 1:
                    return R.drawable.ic_stats_toggle;
                default:
                    return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ManagerTablesFragment.newInstance();
                case 1:
                    return ManagerStatsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Live Orders";
                case 1:
                    return "Stats";
            }
            return null;
        }
    }
}
