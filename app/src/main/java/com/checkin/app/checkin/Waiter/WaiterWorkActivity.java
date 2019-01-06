package com.checkin.app.checkin.Waiter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterWorkActivity extends BaseAccountActivity implements WaiterActiveTableAdapter.onTableInterActionListener {

    List<EventModel> items, itemsDone;
    List<TableModel> tables;

    @BindView(R.id.session_toolbar)
    Toolbar sessionToolbar;
    @BindView(R.id.drawer_open)
    ImageView drawerOpen;
    @BindView(R.id.action_table_menu)
    ImageView actionTableMenu;
    @BindView(R.id.appbar_session)
    AppBarLayout appbarSession;
    @BindView(R.id.rv_active_tables)
    RecyclerView rvActiveTables;
    @BindView(R.id.table_number_container)
    CoordinatorLayout tableNumberContainer;
    @BindView(R.id.image_dine_in)
    ImageView imageDineIn;
    @BindView(R.id.action_dine_in)
    TextView actionDineIn;
    @BindView(R.id.table_user_info)
    CardView tableUserInfo;
    @BindView(R.id.action_menu)
    ImageView actionMenu;
    @BindView(R.id.item_container)
    FrameLayout itemContainer;
    @BindView(R.id.scroll_container)
    ScrollView scrollContainer;
    @BindView(R.id.ivBarcodeScanner)
    ImageView ivBarcodeScanner;
    @BindView(R.id.root_view)
    FrameLayout rootView;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.nav_account)
    NavigationView navAccount;
    @BindView(R.id.nav_table_view)
    NavigationView navTableView;
    @BindView(R.id.drawer_waiter)
    DrawerLayout drawerWaiter;

    private WaiterActiveTableAdapter mWaiterTableAdapter;
    private WaiterItemAdapter mWaiterItemAdapter;
    private WaiterItemAdapter mWaiterItemAdapter2;

    ActiveTableFragment activeTableFragment;
    WaiterEndNavigationTableAdapter waiterEndNavigationTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_work);
        ButterKnife.bind(this);

        setupUI();

        setupUserItem(new ArrayList<>());

        tables = new ArrayList<>();
        setupActiveTables();
    }

    private void setupUI() {
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        //(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_waiter);
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        ActionBarDrawerToggle endToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.messages_drawer_open, R.string.messages_drawer_close);
        drawerLayout.addDrawerListener(endToggle);
        endToggle.syncState();

        activeTableFragment = new ActiveTableFragment();

        drawerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        actionTableMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        getSupportFragmentManager().beginTransaction().add(navTableView.getId(), activeTableFragment).commit();

        ivBarcodeScanner.setOnClickListener(view -> {
            Intent qrScannerIntent = new Intent(getBaseContext(),QRScannerActivity.class);
            startActivity(qrScannerIntent);
        });
    }

    private void setupUserItem(List<EventModel> eventModels) {
        TableItemFragment tableItemFragment = new TableItemFragment();

        getFragmentManager().beginTransaction()
                .replace(itemContainer.getId(), tableItemFragment)
                .commit();

        List<String> list = new ArrayList<>();

        items = new ArrayList<>();
        itemsDone = new ArrayList<>();

        for (int i = 0; i < eventModels.size(); i++) {
            if (eventModels.get(i).getStatus() == EventModel.STATUS.INCOMPLETE) {
                items.add(eventModels.get(i));
            } else
                itemsDone.add(eventModels.get(i));
        }
    }

    public void setupActiveTables() {

        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0)
                tables.add(new TableModel("Table" + i + "", "Standard", true, 4, 2));
        }
        rvActiveTables.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mWaiterTableAdapter = new WaiterActiveTableAdapter(tables);
        mWaiterTableAdapter.setNoItems(items.size());
        mWaiterTableAdapter.setOnTableInterActionListener(this);

        rvActiveTables.setAdapter(mWaiterTableAdapter);

        tables.add(new TableModel("Table" + 15 + "", "Premium", true, 4, 2));
    }

    @Override
    public void selectedTableChanged(int position) {
        mWaiterTableAdapter.setItem_position(position);
        mWaiterTableAdapter.notifyDataSetChanged();
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