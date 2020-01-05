package com.checkin.app.checkin.Cook;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Cook.Fragment.CookTablesFragment;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CookWorkActivity extends BaseAccountActivity {
    public static final String KEY_RESTAURANT_PK = "cook.restaurant_pk";

    @BindView(R.id.drawer_cook_work)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar_cook_work)
    Toolbar toolbar;

    private CookWorkViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_work);
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

        mViewModel = ViewModelProviders.of(this).get(CookWorkViewModel.class);

        initRefreshScreen(R.id.sr_cook_work);

        setupObservers(getIntent().getLongExtra(KEY_RESTAURANT_PK, 0L));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_cook_tables, CookTablesFragment.newInstance())
                .commit();
    }

    private void setupObservers(long shopId) {
        mViewModel.fetchActiveTables(shopId);

        mViewModel.getActiveTables().observe(this, input -> {
            if (input == null) return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                stopRefreshing();
            } else if (input.getStatus() == Resource.Status.LOADING)
                startRefreshing();
            else {
                stopRefreshing();
                Utils.toast(this, input.getMessage());
            }
        });
    }

    @Override
    protected int getDrawerRootId() {
        return R.id.drawer_cook_work;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.drawer_cook_work;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.RESTAURANT_COOK};
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
    }
}
