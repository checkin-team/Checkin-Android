package com.checkin.app.checkin.Waiter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.EndDrawerToggle;

public class WaiterWorkActivity extends BaseAccountActivity implements WaiterActiveTableAdapter.onTableInterActionListener {

    Toolbar toolbarWaiterTable;
    ImageView drawerOpen;
    ImageView actionTableMenu;
    AppBarLayout appbarWaiterTable;
    TabLayout tlWaiterTable;
    ViewPager vpWaiterTable;
    ImageView ivBarcodeScanner;
    Button btnLogout;
    NavigationView navAccount;
    NavigationView navTableView;
    DrawerLayout drawerWaiterTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_work);

        setupUI();
        setupMyWaiterTableViewPager();
        setMyClickListener();
    }

    private void setMyClickListener() {

        actionTableMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerWaiterTable.isDrawerOpen(GravityCompat.END))
                    drawerWaiterTable.closeDrawer(GravityCompat.END);
                else
                    drawerWaiterTable.openDrawer(GravityCompat.END);
            }
        });

        ivBarcodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrScannerIntent = new Intent(getBaseContext(), QRScannerActivity.class);
                startActivity(qrScannerIntent);
            }
        });
    }

    private void setupMyWaiterTableViewPager() {
        /*This is used to setup Tab for waiter table. this will be changed in future.*/
        WaiterTablePagerAdapter mWaiterTablePagerAdapter = new WaiterTablePagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < 8; i++) {
            WaiterTableFragment mWaiterTableFragment = new WaiterTableFragment();
            mWaiterTablePagerAdapter.addWaiterTableFragment(mWaiterTableFragment);
        }

        vpWaiterTable.setAdapter(mWaiterTablePagerAdapter);
        tlWaiterTable.setupWithViewPager(vpWaiterTable);

        createTabIcon();
    }

    private void createTabIcon() {

        for (int i = 0; i < 8; i++) {
            View mView = LayoutInflater.from(this).inflate(R.layout.waiter_table_custom_tab, null);

            FrameLayout fl_waiter_table_tab_active = (FrameLayout) mView.findViewById(R.id.fl_waiter_table_tab_active);

            TextView mtv_waiter_table_tab_title = (TextView) mView.findViewById(R.id.tv_waiter_table_tab_title);
            TextView mtv_waiter_table_tab_number = (TextView) mView.findViewById(R.id.tv_waiter_table_tab_number);
            TextView mtv_waiter_table_tab_active = (TextView) mView.findViewById(R.id.tv_waiter_table_tab_active);

            if(i == 0){
                mtv_waiter_table_tab_active.setText((i+1)+"");
                fl_waiter_table_tab_active.setVisibility(View.VISIBLE);
            }else {
                fl_waiter_table_tab_active.setVisibility(View.GONE);
            }

            mtv_waiter_table_tab_title.setText("TABLE");
            mtv_waiter_table_tab_number.setText((i+1)+"");
            TabLayout.Tab mTab = tlWaiterTable.getTabAt(i);

            if (mTab != null){
                mTab.setCustomView(mView);
            }
        }
    }

    private void setupUI() {

        toolbarWaiterTable = (Toolbar) findViewById(R.id.toolbar_waiter_table);
        drawerOpen = (ImageView) findViewById(R.id.drawer_open);
        actionTableMenu = (ImageView) findViewById(R.id.action_table_menu);
        appbarWaiterTable = (AppBarLayout) findViewById(R.id.appbar_waiter_table);
        tlWaiterTable = (TabLayout) findViewById(R.id.tl_Waiter_Table);
        vpWaiterTable = (ViewPager) findViewById(R.id.vp_waiter_table);
        ivBarcodeScanner = (ImageView) findViewById(R.id.iv_waiter_table_barcode_scanner);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        navAccount = (NavigationView) findViewById(R.id.nav_account);
        navTableView = (NavigationView) findViewById(R.id.nav_table_view);
        drawerWaiterTable = (DrawerLayout) findViewById(R.id.drawer_waiter_table);

        setSupportActionBar(toolbarWaiterTable);

        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerWaiterTable, toolbarWaiterTable, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerWaiterTable.addDrawerListener(startToggle);
        startToggle.syncState();

        EndDrawerToggle endToggle = new EndDrawerToggle(this, drawerWaiterTable, toolbarWaiterTable, R.string.messages_drawer_open, R.string.messages_drawer_close,R.drawable.ic_waiter_table_menu);
        drawerWaiterTable.addDrawerListener(endToggle);
        endToggle.syncState();

        ActiveTableFragment activeTableFragment = new ActiveTableFragment();

        getSupportFragmentManager().beginTransaction().add(navTableView.getId(), activeTableFragment).commit();
    }

    @Override
    public void selectedTableChanged(int position) {
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
}