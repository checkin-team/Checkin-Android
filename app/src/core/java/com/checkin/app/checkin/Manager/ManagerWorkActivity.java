package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Manager.Fragment.ManagerStatsFragment;
import com.checkin.app.checkin.Manager.Fragment.ManagerTablesFragment;
import com.checkin.app.checkin.Manager.Model.ManagerWorkViewModel;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;

public class ManagerWorkActivity extends BaseAccountActivity {
    private static final String TAG = ManagerWorkActivity.class.getSimpleName();

    public static final String KEY_RESTAURANT_PK = "manager.restaurant_pk";

    @BindView(R.id.pager_manager_work)
    DynamicSwipableViewPager pagerManager;
    @BindView(R.id.tabs_manager_work)
    TabLayout tabLayout;
    @BindView(R.id.drawer_manager_work)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar_manager_work)
    Toolbar toolbar;

    public static int EVENT_COUNT = 0;

    private ManagerWorkViewModel mViewModel;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message;
            try {
                message = ((MessageModel) intent.getSerializableExtra(KEY_DATA));
                if (message == null)
                    return;
            } catch (ClassCastException e) {
                Log.e(TAG, "Invalid message object received.");
                e.printStackTrace();
                return;
            }

            switch (message.getType()) {
                case MANAGER_SESSION_NEW:
                    break;
                case MANAGER_SESSION_NEW_ORDER:
                    break;
                case MANAGER_SESSION_EVENT_SERVICE:
                    break;
                case MANAGER_SESSION_CHECKOUT_REQUEST:
                    break;
                case MANAGER_SESSION_EVENT_CONCERN:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_work);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Live Orders");
            actionBar.setDisplayHomeAsUpEnabled(true);

            ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(startToggle);
            startToggle.syncState();
        }

        mViewModel = ViewModelProviders.of(this).get(ManagerWorkViewModel.class);
        mViewModel.fetchActiveTables(getIntent().getLongExtra(KEY_RESTAURANT_PK, 0L));

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
    protected void onResume() {
        super.onResume();
        MESSAGE_TYPE[] types = new MESSAGE_TYPE[] {
                MANAGER_SESSION_NEW, MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
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
