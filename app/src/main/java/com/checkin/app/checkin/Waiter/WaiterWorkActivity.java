package com.checkin.app.checkin.Waiter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableFragment;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.EventBriefModel;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.session.model.TableSessionModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WaiterWorkActivity extends BaseAccountActivity implements
        WaiterTableFragment.WaiterTableInteraction, WaiterEndDrawerTableAdapter.OnTableClickListener {
    public static final String KEY_SHOP_PK = "waiter.shop_pk";
    public static final String KEY_SESSION_PK = "waiter.session_pk";
    public static final String ACTION_NEW_TABLE = "waiter.new_table";
    public static final String KEY_SESSION_QR_ID = "waiter.session_qr_id";

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
    @BindView(R.id.rv_waiter_drawer_inactive_tables)
    RecyclerView rvInactiveTables;
    @BindView(R.id.nav_waiter_end)
    NavigationView navDrawerEnd;

    private WaiterWorkViewModel mViewModel;
    private WaiterTablePagerAdapter mFragmentAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel shop = message.getShopDetail();
            if (shop != null && shop.getPk() != mViewModel.getShopPk())
                return;

            EventBriefModel eventModel;
            BriefModel user;
            SessionOrderedItemModel orderedItemModel;
            long sessionPk;
            switch (message.getType()) {
                case WAITER_SESSION_NEW:
                    String tableName = message.getRawData().getSessionTableName();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    TableSessionModel sessionModel = new TableSessionModel(message.getObject().getPk(), null, eventModel);
                    RestaurantTableModel tableModel = new RestaurantTableModel(RestaurantTableModel.NO_QR_ID, tableName, sessionModel);
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        sessionModel.setHost(user);
                    }
                    WaiterWorkActivity.this.addTable(tableModel);
                    WaiterWorkActivity.this.updateTableStatus(sessionModel.getPk());
                    break;
                case WAITER_SESSION_NEW_ORDER:
                    orderedItemModel = message.getRawData().getSessionOrderedItem();
                    sessionPk = message.getTarget().getPk();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    WaiterWorkActivity.this.addNewOrder(sessionPk, orderedItemModel, eventModel);
                    WaiterWorkActivity.this.updateTableStatus(sessionPk);
                    break;
                case WAITER_SESSION_COLLECT_CASH:
                    sessionPk = message.getObject().getPk();
                    WaiterWorkActivity.this.collectCash(sessionPk, message.getRawData().getSessionBillTotal(), message.getRawData().getSessionBillPaymentMode());
                    WaiterWorkActivity.this.updateTableStatus(sessionPk);
                    break;
                case WAITER_SESSION_EVENT_SERVICE:
                    sessionPk = message.getTarget().getPk();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    WaiterWorkActivity.this.addNewServiceEvent(sessionPk, eventModel, message.getRawData().getSessionEventBrief().getStatus());
                    WaiterWorkActivity.this.updateTableStatus(sessionPk);
                    break;
                case WAITER_SESSION_HOST_ASSIGNED:
                    sessionPk = message.getTarget().getPk();
                    user = message.getObject().getBriefModel();
                    WaiterWorkActivity.this.updateTableHost(sessionPk, user);
                    break;
                case WAITER_SESSION_MEMBER_CHANGE:
                    sessionPk = message.getObject().getPk();
                    int customerCount = message.getRawData().getSessionCustomerCount();
                    WaiterWorkActivity.this.updateTableCustomerCount(sessionPk, customerCount);
                    break;
                case WAITER_SESSION_UPDATE_ORDER:
                case WAITER_SESSION_EVENT_UPDATE:
                    sessionPk = message.getTarget().getPk();
                    long eventId = message.getObject().getPk();
                    WaiterWorkActivity.this.updateEventStatus(sessionPk, eventId, message.getRawData().getSessionEventStatus());
                    WaiterWorkActivity.this.updateTableStatus(sessionPk);
                    break;
                case WAITER_SESSION_END:
                    sessionPk = message.getObject().getPk();
                    WaiterWorkActivity.this.endSession(sessionPk);
                    break;
                case WAITER_SESSION_SWITCH_TABLE:
                    mViewModel.fetchWaiterServedTables();
                    break;
            }
        }
    };
    private long sessionPk;

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

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_NEW_TABLE)) {
            mViewModel.processQrPk(getIntent().getLongExtra(KEY_SESSION_QR_ID, 0L));
        }
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
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        });
    }

    private void fetchData() {
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0L);
        mViewModel.fetchShopTables(shopPk);
        mViewModel.fetchWaiterServedTables();
        mViewModel.fetchWaiterStats();
    }

    private void setupTableFragments() {
        mFragmentAdapter = new WaiterTablePagerAdapter(getSupportFragmentManager());
        pagerTables.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(pagerTables);
        sessionPk = getIntent().getLongExtra(KEY_SESSION_PK, 0L);

        mViewModel.getWaiterTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                stopRefreshing();
                mFragmentAdapter.setTables(tabLayout, listResource.data, this);
                int index = mFragmentAdapter.getTableIndex(sessionPk);
                if (index > 0) pagerTables.setCurrentItem(index, true);
            } else if (listResource.status == Status.LOADING) {
                startRefreshing();
            } else {
                stopRefreshing();
            }
        });

        mViewModel.getQrResult().observe(this, qrResource -> {
            if (qrResource == null)
                return;
            if (qrResource.status == Status.SUCCESS && qrResource.data != null) {
                addWaiterTable(qrResource.data.getSessionPk(), qrResource.data.getTable());
                mViewModel.fetchShopTables(mViewModel.getShopPk());
            } else if (qrResource.status != Status.LOADING) {
                Utils.toast(this, qrResource.message);
            }
        });

        pagerTables.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                WaiterTableModel data = mFragmentAdapter.getTable(position);
                if (data == null) return;
                data.resetEventCount();
                MessageUtils.dismissNotification(WaiterWorkActivity.this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, data.getPk());
                mFragmentAdapter.updateTableStatus(tabLayout, position);
            }
        });
    }

    private void addWaiterTable(long sessionPk, String table) {
        this.sessionPk = sessionPk;
        WaiterTableModel tableModel = new WaiterTableModel(sessionPk, table);
        mViewModel.addWaiterTable(tableModel);
    }

    private void setupShopAssignedTables() {
        final WaiterEndDrawerTableAdapter assignedTableAdapter = new WaiterEndDrawerTableAdapter(null);
        final WaiterEndDrawerTableAdapter unassignedTableAdapter = new WaiterEndDrawerTableAdapter(this);
        final WaiterEndDrawerTableAdapter inactiveTableAdapter = new WaiterEndDrawerTableAdapter(this);
        rvAssignedTables.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvUnassignedTables.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvInactiveTables.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        rvAssignedTables.setAdapter(assignedTableAdapter);
        rvUnassignedTables.setAdapter(unassignedTableAdapter);
        rvInactiveTables.setAdapter(inactiveTableAdapter);

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
        mViewModel.getShopInactiveTables().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null)
                inactiveTableAdapter.setData(listResource.data);
        });
    }

    private void setupUI() {
        initRefreshScreen(R.id.sr_waiter_work);
        setSupportActionBar(toolbarWaiter);

        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarWaiter, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        EndDrawerToggle endToggle = new EndDrawerToggle(this, drawerLayout, toolbarWaiter, R.string.tables_drawer_open, R.string.tables_drawer_close, R.drawable.ic_table_grey);
        drawerLayout.addDrawerListener(endToggle);
        endToggle.syncState();
    }

    @Override
    protected void updateScreen() {
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
        mViewModel.fetchWaiterServedTables();
    }

    // region UI-Update
    private void addTable(RestaurantTableModel tableModel) {
        mViewModel.addRestaurantTable(tableModel);
    }

    private void updateTableStatus(long sessionPk) {
        int index = mFragmentAdapter.getTableIndex(sessionPk);
        WaiterTableModel tableModel = mFragmentAdapter.getTable(index);
        if (tableModel == null) return;
        if (index == pagerTables.getCurrentItem()) {
            tableModel.resetEventCount();
        } else {
            tableModel.increaseEventCount();
        }
        mFragmentAdapter.updateTableStatus(tabLayout, index);
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
            viewModel.initiateCollectCash(sessionBillTotal, sessionBillPaymentMode);
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

    private void updateTableCustomerCount(long sessionPk, int customerCount) {
        WaiterTableViewModel viewModel = mFragmentAdapter.getTableViewModel(sessionPk);
        if (viewModel != null) {
            viewModel.updateMemberCount(customerCount);
        }
    }

    @Override
    public void endSession(long sessionPk) {
        mViewModel.markSessionEnd(sessionPk);
    }

    // endregion

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
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MESSAGE_TYPE[] types = new MESSAGE_TYPE[]{
                MESSAGE_TYPE.WAITER_SESSION_NEW, MESSAGE_TYPE.WAITER_SESSION_NEW_ORDER, MESSAGE_TYPE.WAITER_SESSION_COLLECT_CASH,
                MESSAGE_TYPE.WAITER_SESSION_EVENT_SERVICE, MESSAGE_TYPE.WAITER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.WAITER_SESSION_MEMBER_CHANGE,
                MESSAGE_TYPE.WAITER_SESSION_UPDATE_ORDER, MESSAGE_TYPE.WAITER_SESSION_END, MESSAGE_TYPE.WAITER_SESSION_EVENT_UPDATE,
                MESSAGE_TYPE.WAITER_SESSION_SWITCH_TABLE
        };
        getAccountViewModel().updateResults();
        mViewModel.updateResults();
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

    @SuppressLint("WrongConstant")
    @Override
    public void onTableClick(RestaurantTableModel restaurantTableModel) {
        drawerLayout.closeDrawer(Gravity.END,false);
        newWaiterSessionDialog(restaurantTableModel.getQrPk(), restaurantTableModel.getTable());
    }

    private void newWaiterSessionDialog(long qrPk, String tableName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(tableName)
                .setMessage("Do you want to be host of this table?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mViewModel.processQrPk(qrPk);
                }).setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private static class WaiterTablePagerAdapter extends FragmentStatePagerAdapter {
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

        @Override
        public int getItemPosition(@NonNull Object object) {
            WaiterTableFragment fragment = ((WaiterTableFragment) object);
            int index = mFragmentList.indexOf(fragment);
            if (index > -1) return index;
            else return POSITION_NONE;
        }

        void resetTables() {
            mTableList.clear();
            mFragmentList.clear();
            notifyDataSetChanged();
        }

        void setTables(TabLayout tabLayout, List<WaiterTableModel> tableModels, WaiterTableFragment.WaiterTableInteraction listener) {
            resetTables();
            for (WaiterTableModel tableModel : tableModels) {
                mTableList.add(tableModel);
                mFragmentList.add(WaiterTableFragment.newInstance(tableModel.getPk(), listener));
            }
            updateTabUi(tabLayout);
        }

        void updateTabUi(TabLayout tabLayout) {
            notifyDataSetChanged();
            for (int i = 0, length = mTableList.size(); i < length; i++)
                setTabCustomView(tabLayout, i, mTableList.get(i));
        }

        void addTable(TabLayout tabLayout, WaiterTableModel tableModel, WaiterTableFragment.WaiterTableInteraction listener) {
            mTableList.add(tableModel);
            mFragmentList.add(WaiterTableFragment.newInstance(tableModel.getPk(), listener));
            updateTabUi(tabLayout);
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
                this.updateTabView(view, tableModel.getTable(), tableModel.formatEventCount());
                tab.setCustomView(view);
            }
        }

        int getTableIndex(long tableId) {
            int index = -1;
            if (tableId == 0) return index;
            for (int i = 0, length = mTableList.size(); i < length; i++) {
                if (mTableList.get(i).getPk() == tableId) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        @Nullable
        WaiterTableModel getTable(int index) {
            if (index < 0)
                return null;
            return mTableList.get(index);
        }

        void updateTableStatus(TabLayout tabLayout, int index) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            WaiterTableModel data = mTableList.get(index);
            if (tab != null && tab.getCustomView() != null) {
                if (data.getEventCount() > 0) {
                    WaiterTableFragment fragment = mFragmentList.get(index);
                    mFragmentList.remove(index);
                    mTableList.remove(index);
                    mTableList.add(0, data);
                    mFragmentList.add(0, fragment);
                    updateTabUi(tabLayout);
                } else {
                    this.updateTabView(tab.getCustomView(), data.getTable(), null);
                }
            }
        }

        private void updateTabView(View view, String title, String badge) {
            TextView tvTitle = view.findViewById(R.id.tv_tab_title);
            TextView tvBadge = view.findViewById(R.id.tv_tab_badge);
            tvTitle.setText(title);
            if (badge != null) {
                tvBadge.setText(badge);
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                tvBadge.setVisibility(View.GONE);
            }
        }
    }
}