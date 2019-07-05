package com.checkin.app.checkin.Manager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Adapter.ManagerInactiveTableAdapter;
import com.checkin.app.checkin.Manager.Fragment.ManagerInvoiceFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerStatsFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerTablesActivateFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerTablesFragment;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel;
import com.checkin.app.checkin.Shop.ShopPreferences;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerWorkActivity extends BaseAccountActivity implements ManagerTablesActivateFragment.LiveOrdersInteraction, ManagerInactiveTableAdapter.ManagerTableInitiate {
    public static final String KEY_OPEN_LAST_TABLES = "manager.show_last_tables";
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
    @BindView(R.id.tv_action_bar_title)
    TextView tvActionBarTitle;
    @BindView(R.id.sw_live_order)
    SwitchCompat swLiveOrdersToggle;
    @BindView(R.id.container_manager_inactive_tables)
    ViewGroup managerTablesParentContainer;
    @BindView(R.id.ll_manager_inactive_tables)
    ViewGroup managerTablesContainer;
    @BindView(R.id.rv_mw_table)
    RecyclerView rvTable;
    private ManagerWorkViewModel mViewModel;
    private ManagerInactiveTableAdapter mInactiveAdapter;
    private ShopInvoiceViewModel mShopViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_work);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setElevation(getResources().getDimension(R.dimen.card_elevation));
            actionBar.setDisplayHomeAsUpEnabled(true);

            ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(startToggle);
            startToggle.syncState();
        }

        rvTable.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mInactiveAdapter = new ManagerInactiveTableAdapter(this);
        rvTable.setAdapter(mInactiveAdapter);

        mViewModel = ViewModelProviders.of(this).get(ManagerWorkViewModel.class);
        mShopViewModel = ViewModelProviders.of(this).get(ShopInvoiceViewModel.class);

        initRefreshScreen(R.id.sr_manager_work);

        setupObservers(getIntent().getLongExtra(KEY_RESTAURANT_PK, 0L));

        pagerManager.setEnabled(false);
        ManagerFragmentAdapter pagerAdapter = new ManagerFragmentAdapter(getSupportFragmentManager());
        pagerManager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pagerManager);
        pagerAdapter.setupWithTab(tabLayout, pagerManager);
        pagerManager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    if (position == 0) {
                        tvActionBarTitle.setText("Live Orders");
                    } else if (position == 1) {
                        tvActionBarTitle.setText("Insight");
                    } else if (position == 2) {
                        tvActionBarTitle.setText("Invoice");
                    }
                }
            }
        });

        swLiveOrdersToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pagerAdapter.setActivated(isChecked);
            pagerAdapter.setupWithTab(tabLayout, pagerManager);
            ShopPreferences.setManagerLiveOrdersActivated(this, isChecked);
        });

        setLiveOrdersActivation(ShopPreferences.isManagerLiveOrdersActivated(this));


        managerTablesParentContainer.setOnClickListener(v -> {
            managerTablesParentContainer.setVisibility(View.GONE);
        });

        Utils.calculateHeightSetHalfView(this, managerTablesContainer);

        boolean shouldOpenInvoice = getIntent().getBooleanExtra(KEY_OPEN_LAST_TABLES, false);
        if (shouldOpenInvoice) {
            pagerManager.setCurrentItem(2, true);
        } else {
            Bundle sessionBundle = getIntent().getBundleExtra(KEY_SESSION_BUNDLE);
            if (sessionBundle != null) {
                Intent intent = new Intent(this, ManagerSessionActivity.class);
                intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, sessionBundle.getLong(ManagerSessionActivity.KEY_SESSION_PK))
                        .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk())
                        .putExtra(ManagerSessionActivity.KEY_OPEN_ORDERS, sessionBundle.getBoolean(ManagerSessionActivity.KEY_OPEN_ORDERS));
                startActivity(intent);
            }
        }
    }

    private void setupObservers(long shopId) {
        mViewModel.fetchActiveTables(shopId);

        mShopViewModel.fetchShopSessions(shopId);
        mShopViewModel.filterFrom(Utils.getCurrentFormattedDateInvoice());
        mShopViewModel.filterTo(Utils.getCurrentFormattedDateInvoice());

        mViewModel.getActiveTables().observe(this, input -> {
            if (input == null) return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                stopRefreshing();
            } else if (input.status == Resource.Status.LOADING)
                startRefreshing();
            else {
                stopRefreshing();
                Utils.toast(this, input.message);
            }
        });

        mViewModel.getInactiveTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                mInactiveAdapter.setData(listResource.data);
        });
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
    public void setLiveOrdersActivation(boolean isActivated) {
        swLiveOrdersToggle.setChecked(isActivated);
    }

    @Override
    protected void updateScreen() {
        super.updateScreen();
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
        mViewModel.fetchStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());

    }

    @OnClick(R.id.im_manager_initiate_session)
    public void onClickInitiate() {
        if (mInactiveAdapter.getItemCount() > 0)
            managerTablesParentContainer.setVisibility(View.VISIBLE);
        else
            Utils.toast(this, "No tables are Inactive.");
    }

    @Override
    public void onClickInactiveTable(RestaurantTableModel tableModel) {
        managerTablesParentContainer.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(tableModel.getTable())
                .setMessage("Do you want to initiate the session?")
                .setPositiveButton("Done", (dialog, which) -> mViewModel.processQrPk(tableModel.getQrPk()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (managerTablesParentContainer.getVisibility() == View.VISIBLE)
            managerTablesParentContainer.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    static class ManagerFragmentAdapter extends BaseFragmentAdapterBottomNav {

        private boolean isActivated = true;

        private ManagerTablesFragment mTableFragment = ManagerTablesFragment.newInstance();
        private ManagerTablesActivateFragment mActiveTableFragment = ManagerTablesActivateFragment.newInstance();


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
                case 2:
                    return R.drawable.ic_invoice_toggle;
                default:
                    return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (isActivated)
                        return mTableFragment;
                    else
                        return mActiveTableFragment;
                case 1:
                    return ManagerStatsFragment.newInstance();
                case 2:
                    return ManagerInvoiceFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Live Orders";
                case 1:
                    return "Insight";
                case 2:
                    return "Invoice";
            }
            return null;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object.getClass() == ManagerStatsFragment.class)
                return super.getItemPosition(object);
            else
                return POSITION_NONE;
        }

        void setActivated(boolean isChecked) {
            isActivated = isChecked;
            notifyDataSetChanged();
        }
    }
}
