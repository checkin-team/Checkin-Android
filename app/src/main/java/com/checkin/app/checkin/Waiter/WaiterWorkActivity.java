package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableEventFragment;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WaiterWorkActivity extends BaseAccountActivity {
    public static final String KEY_SHOP_PK = "waiter.shop_pk";
    private static final int REQUEST_QR_SCANNER = 121;

    @BindView(R.id.toolbar_waiter)
    Toolbar toolbarWaiter;
    @BindView(R.id.drawer_waiter_work)
    DrawerLayout drawerLayout;
    @BindView(R.id.tabs_waiter)
    TabLayout tabLayout;
    @BindView(R.id.pager_waiter)
    DynamicSwipableViewPager pagerTables;
    @BindView(R.id.rv_waiter_drawer_assigned_tables)
    RecyclerView rvAssignedTables;
    @BindView(R.id.rv_waiter_drawer_unassigned_tables)
    RecyclerView rvUnassignedTables;

    private WaiterWorkViewModel mViewModel;
    private WaiterTablePagerAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_work);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(WaiterWorkViewModel.class);
        setupUI();
        setupShopAssignedTables();
        setupTableFragments();
        fetchData();
    }

    private void fetchData() {
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopActiveTables(shopPk);
        mViewModel.fetchWaiterServedTables();
    }

    private void setupTableFragments() {
        mFragmentAdapter = new WaiterTablePagerAdapter(getSupportFragmentManager());
        pagerTables.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(pagerTables);

        mViewModel.getWaiterTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                mFragmentAdapter.setTables(tabLayout, listResource.data);
            }
        });

        mViewModel.getQrResult().observe(this, qrResource -> {
            if (qrResource == null)
                return;
            if (qrResource.status == Status.SUCCESS && qrResource.data != null) {
                mFragmentAdapter.addTable(tabLayout, new WaiterTableModel(qrResource.data.getSessionPk(), qrResource.data.getTable()));
            } else if (qrResource.status != Status.LOADING) {
                Utils.toast(this, qrResource.message);
            }
        });
    }

    private void setupShopAssignedTables() {
        final WaiterEndDrawerTableAdapter assignedTableAdapter = new WaiterEndDrawerTableAdapter();
        final WaiterEndDrawerTableAdapter unassignedTableAdapter = new WaiterEndDrawerTableAdapter();
        rvAssignedTables.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUnassignedTables.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rvAssignedTables.setAdapter(assignedTableAdapter);
        rvUnassignedTables.setAdapter(unassignedTableAdapter);

        mViewModel.getShopAssignedTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null)
                assignedTableAdapter.setData(listResource.data);
        });
        mViewModel.getShopUnassignedTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null)
                unassignedTableAdapter.setData(listResource.data);
        });
    }

    private void setupUI() {
        setSupportActionBar(toolbarWaiter);

        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarWaiter, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        EndDrawerToggle endToggle = new EndDrawerToggle(this, drawerLayout, toolbarWaiter, R.string.tables_drawer_open, R.string.tables_drawer_close, R.drawable.ic_table_grey);
        drawerLayout.addDrawerListener(endToggle);
        endToggle.syncState();
    }

    @OnClick(R.id.im_waiter_scanner)
    public void onClickScanner(View v) {
        Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
        startActivityForResult(intent, REQUEST_QR_SCANNER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR_SCANNER && resultCode == RESULT_OK) {
            String qrData = data.getStringExtra(QRScannerActivity.KEY_QR_RESULT);
            mViewModel.processQr(qrData);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected int getNavMenu() {
        return R.menu.menu_waiter_work;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.RESTAURANT_WAITER};
    }

    private static class WaiterTablePagerAdapter extends FragmentStatePagerAdapter {
        private List<WaiterTableEventFragment> mFragmentList = new ArrayList<>();
        private List<WaiterTableModel> mTableList = new ArrayList<>();

        WaiterTablePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTableList.get(position).getTable();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void setTables(TabLayout tabLayout, List<WaiterTableModel> tableModels) {
            for (WaiterTableModel tableModel: tableModels)
                addTable(tabLayout, tableModel);
        }

        public void addTable(TabLayout tabLayout, WaiterTableModel tableModel) {
            mTableList.add(tableModel);
            mFragmentList.add(WaiterTableEventFragment.newInstance(tableModel.getPk()));
            notifyDataSetChanged();
            setTabCustomView(tabLayout, mTableList.size() - 1, tableModel);
        }

        private void setTabCustomView(TabLayout tabLayout, int index, WaiterTableModel tableModel) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                View view = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.view_tab_badge, null, false);
                this.updateTabView(view, tableModel.getTable(), null);
                tab.setCustomView(view);
            }
        }

        public void updateTabCount(TabLayout tabLayout, long tableId, int count) {
            int index = -1;
            String title = "";
            for (int i = 0; i < mTableList.size(); i++) {
                if (mTableList.get(i).getPk() == tableId) {
                    index = i;
                    title = mTableList.get(i).getTable();
                    break;
                }
            }
            if (index == -1)
                return;
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null && tab.getCustomView() != null) {
                if (count > 0) {
                    this.updateTabView(tab.getCustomView(), title, String.valueOf(count));
                } else {
                    this.updateTabView(tab.getCustomView(), title, null);
                }
            }
        }

        private void updateTabView(View view, String title, String badge) {
            TextView tvTitle = view.findViewById(R.id.tv_tab_title);
            TextView tvBadge = view.findViewById(R.id.tv_tab_badge);
            tvTitle.setText(title);
            if (badge != null)
                tvBadge.setText(badge);
            else
                tvBadge.setVisibility(View.GONE);
        }
    }
}