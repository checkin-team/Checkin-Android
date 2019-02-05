package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableFragment;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;

public class WaiterWorkActivity extends BaseAccountActivity implements WaiterTableFragment.WaiterTableInteraction {
    private static final String TAG = WaiterWorkActivity.class.getSimpleName();

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
            EventBriefModel eventModel;
            BriefModel user;
            SessionOrderedItemModel orderedItemModel;
            long sessionPk;
            switch (message.getType()) {
                case WAITER_SESSION_NEW:
                    String tableName = message.getRawData().getSessionTableName();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    RestaurantTableModel tableModel = new RestaurantTableModel(message.getObject().getPk(), tableName, null, eventModel);
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        tableModel.setHost(user);
                    }
                    WaiterWorkActivity.this.addTable(tableModel);
                    break;
                case WAITER_SESSION_NEW_ORDER:
                    orderedItemModel = message.getRawData().getSessionOrderedItem();
                    sessionPk = message.getTarget().getPk();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    WaiterWorkActivity.this.addNewOrder(sessionPk, orderedItemModel, eventModel);
                    break;
                case WAITER_SESSION_COLLECT_CASH:
                    sessionPk = message.getObject().getPk();
                    WaiterWorkActivity.this.collectCash(sessionPk, message.getRawData().getSessionBillTotal(), message.getRawData().getSessionBillPaymentMode());
                    break;
                case WAITER_SESSION_EVENT_SERVICE:
                    sessionPk = message.getTarget().getPk();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    WaiterWorkActivity.this.addNewServiceEvent(sessionPk, eventModel, message.getRawData().getSessionEventBrief().getStatus());
                    break;
                case WAITER_SESSION_HOST_ASSIGNED:
                    sessionPk = message.getTarget().getPk();
                    user = message.getObject().getBriefModel();
                    WaiterWorkActivity.this.updateTableHost(sessionPk, user);
                    break;
                case WAITER_SESSION_MEMBER_CHANGE:
                    sessionPk = message.getTarget().getPk();
                    int customerCount = message.getRawData().getSessionCustomerCount();
                    WaiterWorkActivity.this.updateTableCount(sessionPk, customerCount);
                    break;
                case WAITER_SESSION_UPDATE_ORDER:
                    sessionPk = message.getTarget().getPk();
                    long eventId = message.getObject().getPk();
                    WaiterWorkActivity.this.updateEventStatus(sessionPk, eventId, message.getRawData().getSessionEventStatus());
                    break;
                case WAITER_SESSION_END:
                    sessionPk = message.getObject().getPk();
                    WaiterWorkActivity.this.endSession(sessionPk);
                    break;
            }
        }
    };

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
        setupDrawer();
    }

    private void setupDrawer() {
        mViewModel.getWaiterStats().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                WaiterStatsModel data = resource.data;
                Menu menu = getNavAccount().getMenu();
                try {
                    menu.getItem(0).setTitle(String.format(
                            Locale.ENGLISH, getString(R.string.menu_waiter_orders_taken), data.formatOrdersTaken()));
                    menu.getItem(1).setTitle(String.format(
                            Locale.ENGLISH, getString(R.string.menu_waiter_total_tip), data.formatTotalTip(this)));
                } catch (IndexOutOfBoundsException ignored) {}
            }
        });
    }

    private void fetchData() {
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopActiveTables(shopPk);
        mViewModel.fetchWaiterServedTables();
        mViewModel.fetchWaiterStats();
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
                mViewModel.updateResults();
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
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                assignedTableAdapter.setData(listResource.data);
            }
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


    private void addTable(RestaurantTableModel tableModel) {
        mViewModel.addRestaurantTable(tableModel);
    }

    private void addNewOrder(long sessionPk, SessionOrderedItemModel orderedItemModel, EventBriefModel eventBriefModel) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            WaiterEventModel eventModel = WaiterEventModel.fromEventBriefModel(eventBriefModel);
            eventModel.setStatus(orderedItemModel.getStatus());
            eventModel.setOrderedItem(orderedItemModel);
            viewModel.addNewEvent(eventModel);
        }
    }


    private void collectCash(long sessionPk, double sessionBillTotal, ShopModel.PAYMENT_MODE sessionBillPaymentMode) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            viewModel.initiateCollectCash(sessionBillTotal);
        }
    }

    private void updateTableHost(long sessionPk, BriefModel host) {
        mViewModel.updateShopTable(sessionPk, host);
    }


    private void updateEventStatus(long sessionPk, long eventId, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            viewModel.updateOrderItemStatus(eventId, sessionEventStatus);
        }
    }

    private void addNewServiceEvent(long sessionPk, EventBriefModel eventModel, SessionChatModel.CHAT_STATUS_TYPE status) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            WaiterEventModel waiterEventModel = WaiterEventModel.fromEventBriefModel(eventModel);
            waiterEventModel.setStatus(status);
            viewModel.addNewEvent(waiterEventModel);
        }
    }

    private void updateTableCount(long sessionPk, int customerCount) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            viewModel.updateMemberCount(customerCount);
        }
    }

    private void endSession(long sessionPk) {
        mViewModel.markSessionEnd(sessionPk);
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
    protected void onResume() {
        super.onResume();
        MESSAGE_TYPE[] types = new MESSAGE_TYPE[] {
                MESSAGE_TYPE.WAITER_SESSION_NEW, MESSAGE_TYPE.WAITER_SESSION_NEW_ORDER, MESSAGE_TYPE.WAITER_SESSION_COLLECT_CASH,
                MESSAGE_TYPE.WAITER_SESSION_EVENT_SERVICE, MESSAGE_TYPE.WAITER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.WAITER_SESSION_MEMBER_CHANGE,
                MESSAGE_TYPE.WAITER_SESSION_UPDATE_ORDER, MESSAGE_TYPE.WAITER_SESSION_END
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    @Override
    protected int getDrawerRootId() {
        return R.id.drawer_waiter_work;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.drawer_waiter_work;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.RESTAURANT_WAITER};
    }

    @Override
    public void onTableActiveEventCount(long sessionPk, int count) {
        mFragmentAdapter.updateTabCount(tabLayout, sessionPk, count);
    }

    private class WaiterTablePagerAdapter extends FragmentStatePagerAdapter {
        private List<WaiterTableFragment> mFragmentList = new ArrayList<>();
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

        void setTables(TabLayout tabLayout, List<WaiterTableModel> tableModels) {
            for (WaiterTableModel tableModel: tableModels)
                addTable(tabLayout, tableModel);
        }

        void addTable(TabLayout tabLayout, WaiterTableModel tableModel) {
            mTableList.add(tableModel);
            mFragmentList.add(WaiterTableFragment.newInstance(tableModel.getPk(), WaiterWorkActivity.this));
            notifyDataSetChanged();
            setTabCustomView(tabLayout, mTableList.size() - 1, tableModel);
        }

        @Nullable
        WaiterTableViewModel getTableViewModel(long tablePk) {
            int pos = -1;
            for (int i = 0, count = mTableList.size(); i < count; i++) {
                if (mTableList.get(i).getPk() == tablePk) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                WaiterTableFragment fragment = mFragmentList.get(pos);
                return fragment.getViewModel();
            }
            return null;
        }

        private void setTabCustomView(TabLayout tabLayout, int index, WaiterTableModel tableModel) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                View view = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.view_tab_badge, null, false);
                this.updateTabView(view, tableModel.getTable(), null);
                tab.setCustomView(view);
            }
        }

        void updateTabCount(TabLayout tabLayout, long tableId, int count) {
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