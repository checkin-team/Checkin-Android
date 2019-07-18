package com.checkin.app.checkin.session.activesession;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.UserMenu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Utility.OnBoardingUtils;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.chat.SessionChatActivity;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.ActiveSessionModel;
import com.checkin.app.checkin.session.model.SessionCustomerModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.session.model.TrendingDishModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionActivity extends BaseActivity implements
        ActiveSessionMemberAdapter.SessionMemberInteraction, ActiveSessionTrendingDishAdapter.SessionTrendingDishInteraction {

    public static final String KEY_SP_INTERACT_WITH_US = "sp.interact.with.us";
    public static final String KEY_INTERACT_WITH_US = "interact.with.us";
    public static final String SP_MENU = "sp.menu";
    private static final int RC_SEARCH_MEMBER = 201;

    @BindView(R.id.shimmer_session_members)
    ShimmerFrameLayout shimmerMembers;
    @BindView(R.id.shimmer_as_trending_dishes)
    ShimmerFrameLayout shimmerTrendingDish;
    @BindView(R.id.sr_active_session)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_session_members)
    RecyclerView rvMembers;
    @BindView(R.id.rv_as_trending_dishes)
    RecyclerView rvTrendingDishes;
    @BindView(R.id.tv_active_session_bill)
    TextView tvBill;
    @BindView(R.id.tv_as_waiter_name)
    TextView tvWaiterName;
    @BindView(R.id.tv_session_live_at)
    TextView tvSessionLiveAt;
    @BindView(R.id.im_as_waiter_pic)
    ImageView imWaiterPic;
    @BindView(R.id.container_as_actions_bottom)
    ViewGroup containerBottomActions;
    @BindView(R.id.container_as_chat_action)
    ViewGroup containerChatActions;
    @BindView(R.id.tv_as_order_new_count)
    TextView tvCountOrdersNew;
    @BindView(R.id.tv_as_order_progress_count)
    TextView tvCountOrdersInProgress;
    @BindView(R.id.tv_as_order_done_count)
    TextView tvCountOrdersDelivered;
    @BindView(R.id.tv_session_checkout)
    TextView tvSessionCheckout;
    @BindView(R.id.btn_active_session_menu)
    ImageView btnSessionMenu;
    @BindView(R.id.rl_container_session_orders)
    RelativeLayout rlSessionOrders;
    @BindView(R.id.ll_call_waiter_button)
    ViewGroup llCallWaiter;
    @BindView(R.id.ll_table_cleaning_button)
    LinearLayout llTableCleaning;
    @BindView(R.id.ll_refill_glass_button)
    LinearLayout llRefillGlass;
    @BindView(R.id.tv_interact_with_us)
    TextView tvInteractWithUs;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;
    private ActiveSessionTrendingDishAdapter mTrendingDishAdapter;
    private ActiveSessionViewOrdersFragment mOrdersFragment;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel model;
            SessionChatModel event;
            switch (message.getType()) {
                case USER_SESSION_BILL_CHANGE:
                    ActiveSessionActivity.this.updateBill(message.getRawData().getSessionBillTotal());
                    break;
                case USER_SESSION_HOST_ASSIGNED:
                    model = message.getObject();
                    ActiveSessionActivity.this.updateHost(model.getBriefModel());
                    break;
                case USER_SESSION_MEMBER_ADD_REQUEST:
                    model = message.getActor();
                    SessionCustomerModel customer = new SessionCustomerModel(model.getPk(), model.getBriefModel(), false, false);
                    ActiveSessionActivity.this.addCustomer(customer);
                    break;
                case USER_SESSION_MEMBER_ADDED:
                    model = message.getObject();
                    ActiveSessionActivity.this.updateCustomer(model.getPk(), true);
                    break;
                case USER_SESSION_ORDER_NEW:
                    ActiveSessionActivity.this.addNewOrder(message.getRawData().getSessionOrderedItem());
                    break;
                case USER_SESSION_EVENT_NEW:
                    event = message.getRawData().getSessionEventDetail();
                    if (event.getType() == SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT)
                        ActiveSessionActivity.this.sessionRequestedCheckout();
                    break;
                case USER_SESSION_UPDATE_ORDER:
                    long orderPk = message.getRawData().getSessionOrderId();
                    ActiveSessionActivity.this.updateOrderStatus(orderPk, message.getRawData().getSessionEventStatus());
                    break;
                case USER_SESSION_END:
                    Utils.navigateBackToHome(getApplicationContext());
                    break;
                case USER_SESSION_MEMBER_REMOVED:
                    model = message.getObject();
                    ActiveSessionActivity.this.updateCustomer(model.getPk(), false);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);

        setupUi();
        setupObservers();

        mViewModel.fetchActiveSessionDetail();
        mViewModel.fetchSessionOrders();
        mOrdersFragment = ActiveSessionViewOrdersFragment.newInstance();
        explainSession();
    }

    private void setupObservers() {
        mViewModel.getSessionData().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.getData();
            switch (resource.getStatus()) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    stopRefreshing();
                    mViewModel.setSessionPk(data.getPk());
                    mViewModel.setShopPk(data.getShopPk());
                    mViewModel.fetchTrendingItem();
                    setupData(data);
                    break;
                }
                case LOADING: {
                    startRefreshing();
                    break;
                }
                case ERROR_NOT_FOUND:
                    finish();
                    break;
                default: {
                    stopRefreshing();
                    Utils.toast(this, resource.getMessage());
                    break;
                }
            }
        });

        mViewModel.getSessionMemberUpdate().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.getStatus()) {
                case SUCCESS: {
                    Utils.toast(this, "Done!");
                    mViewModel.fetchActiveSessionDetail();
                    break;
                }
                case LOADING:
                    break;
                case ERROR_NOT_FOUND:
                    mViewModel.fetchActiveSessionDetail();
                default: {
                    Utils.toast(this, resource.getMessage());
                }
            }
        });

        mViewModel.getCountNewOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersNew.setTextColor(
                    integer > 0 ? getResources().getColor(R.color.primary_red) : getResources().getColor(R.color.brownish_grey));
            tvCountOrdersNew.setText(String.valueOf(integer));
        });
        mViewModel.getCountProgressOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersInProgress.setTextColor(
                    integer > 0 ? getResources().getColor(R.color.primary_red) : getResources().getColor(R.color.brownish_grey));
            tvCountOrdersInProgress.setText(String.valueOf(integer));
        });
        mViewModel.getCountDeliveredOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersDelivered.setText(String.valueOf(integer));
        });


        mViewModel.getMenuTrendingItems().observe(this, inventoryItemModels -> {
            if (inventoryItemModels == null)
                return;

            if (inventoryItemModels.getStatus() == Resource.Status.SUCCESS && inventoryItemModels.getData() != null) {
                mTrendingDishAdapter.setData(inventoryItemModels.getData());
                shimmerTrendingDish.stopShimmer();
                shimmerTrendingDish.setVisibility(View.GONE);
            }
        });
    }

    public void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUi() {
        rvMembers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mSessionMembersAdapter = new ActiveSessionMemberAdapter(null, this);
        rvMembers.setAdapter(mSessionMembersAdapter);
        rvMembers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE)
                    enableDisableSwipeRefresh(false);
                else
                    enableDisableSwipeRefresh(true);
            }
        });

        rvTrendingDishes.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mTrendingDishAdapter = new ActiveSessionTrendingDishAdapter(this);
        rvTrendingDishes.setAdapter(mTrendingDishAdapter);
        rvTrendingDishes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE)
                    enableDisableSwipeRefresh(false);
                else
                    enableDisableSwipeRefresh(true);
            }
        });

        tvBill.setEnabled(false);
        rlSessionOrders.setEnabled(false);
        containerBottomActions.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE, containerBottomActions);
            return true;
        });

        initRefreshScreen(R.id.sr_active_session);
    }

    private void explainSession() {
        OnBoardingUtils.conditionalOnBoarding(this, SP_MENU, true, new OnBoardingUtils.OnBoardingModel("Checkout your menu here!", btnSessionMenu, false));
    }

    // region UI-Update

    private void setupData(ActiveSessionModel data) {
        mSessionMembersAdapter.setUsers(data.getCustomers());
        tvBill.setText(data.formatBill(this));
        tvBill.setEnabled(true);
        tvSessionLiveAt.setText(Html.fromHtml(data.getRestaurant().formatRestaurantName()), TextView.BufferType.SPANNABLE);

        if (data.getHost() != null) {
            tvWaiterName.setText(data.getHost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
        if (!data.isRequestedCheckout()) {
            btnSessionMenu.setEnabled(true);
//            mTrendingDishAdapter.isSessionCheckedOut = false;
            rlSessionOrders.setEnabled(true);
            rlSessionOrders.setVisibility(View.VISIBLE);
        } else {
            btnSessionMenu.setEnabled(false);
//            mTrendingDishAdapter.isSessionCheckedOut = true;
            tvSessionCheckout.setVisibility(View.VISIBLE);
            rlSessionOrders.setVisibility(View.GONE);
        }
        shimmerMembers.stopShimmer();
        shimmerMembers.setVisibility(View.GONE);
    }

    private void updateBill(double bill) {
        mViewModel.updateBill(bill);
    }

    private void updateHost(BriefModel user) {
        mViewModel.updateHost(user);
    }

    private void addCustomer(SessionCustomerModel customer) {
        mViewModel.addCustomer(customer);
    }

    private void updateCustomer(long customer, boolean isAdded) {
        mViewModel.updateCustomer(customer, isAdded);
    }


    private void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        mViewModel.updateOrderStatus(orderPk, sessionEventStatus);
    }

    private void sessionRequestedCheckout() {
        mViewModel.setRequestedCheckout(true);
    }

    // endregion

    // region Click Listeners

    private void addNewOrder(SessionOrderedItemModel sessionOrderedItem) {
        mViewModel.addNewOrder(sessionOrderedItem);
    }

    @OnClick(R.id.im_as_back)
    public void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        if (Utils.isNetworkConnected(this)) {
            if (mViewModel.getShopPk() < 0)
                return;
            btnSessionMenu.setEnabled(false);
            SessionMenuActivity.Companion.startWithSession(this, mViewModel.getShopPk(), null, null);
        } else {
            Utils.toast(this, R.string.error_unavailable_network);
        }
    }

    @OnClick(R.id.rl_container_session_orders)
    public void onViewOrders() {
        if (Utils.isNetworkConnected(this)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_as_orders, mOrdersFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Utils.toast(this, R.string.error_unavailable_network);
        }
    }

    @OnClick({R.id.tv_active_session_bill, R.id.container_bottom_total_price})
    public void openBillDetails() {
        if (Utils.isNetworkConnected(this)) {
            startActivity(new Intent(
                    this, ActiveSessionInvoiceActivity.class)
                    .putExtra(ActiveSessionInvoiceActivity.KEY_SESSION_REQUESTED_CHECKOUT, mViewModel.isRequestedCheckout()));
        } else {
            Utils.toast(this, R.string.error_unavailable_network);
        }
    }

    @OnClick(R.id.ll_call_waiter_button)
    public void openChatCallWaiter() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER, llCallWaiter);
    }

    @OnClick(R.id.ll_table_cleaning_button)
    public void openChatCleanContainer() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE, llTableCleaning);
    }

    @OnClick(R.id.ll_refill_glass_button)
    public void openChatRefillGlass() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY, llRefillGlass);
    }

    // endregion

    @Override
    public void onUnacceptedMemberClicked(SessionCustomerModel customerModel) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to accept/reject member request?")
                .setPositiveButton("Accept", ((dialog, which) -> mViewModel.acceptSessionMember(customerModel.getUser().getPk())))
                .setNegativeButton("Reject", ((dialog, which) -> mViewModel.removeSessionMember(customerModel.getUser().getPk())))
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SEARCH_MEMBER && resultCode == RESULT_OK) {
            long userPk = data.getLongExtra(SearchActivity.KEY_RESULT_PK, 0L);
            mViewModel.addMembers(userPk);
        }
    }

    public void openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE service, View view) {
        view.setEnabled(false);
        Intent myIntent = new Intent(ActiveSessionActivity.this, SessionChatActivity.class);
        myIntent.putExtra(SessionChatActivity.KEY_SERVICE_TYPE, service.tag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, (View) containerChatActions, "chatActions");
            startActivity(myIntent, options.toBundle());
        } else {
            startActivity(myIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MESSAGE_TYPE[] types = new MESSAGE_TYPE[]{
                MESSAGE_TYPE.USER_SESSION_BILL_CHANGE, MESSAGE_TYPE.USER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.USER_SESSION_ORDER_NEW,
                MESSAGE_TYPE.USER_SESSION_MEMBER_ADD_REQUEST, MESSAGE_TYPE.USER_SESSION_MEMBER_ADDED, MESSAGE_TYPE.USER_SESSION_END,
                MESSAGE_TYPE.USER_SESSION_EVENT_NEW, MESSAGE_TYPE.USER_SESSION_UPDATE_ORDER
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
        updateScreen();
        resetEnableViews();
        if (OnBoardingUtils.isOnBoardingShown(this, KEY_INTERACT_WITH_US) || OnBoardingUtils.isOnBoardingShown(this, SP_MENU))
            OnBoardingUtils.conditionalOnBoarding(this, KEY_SP_INTERACT_WITH_US, true, new OnBoardingUtils.OnBoardingModel("Interact with waiter here!", tvInteractWithUs, true));
        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());
    }

    @Override
    public void onBackPressed() {
        if (!mOrdersFragment.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    public void resetEnableViews() {
        containerBottomActions.setEnabled(true);
        llCallWaiter.setEnabled(true);
        llTableCleaning.setEnabled(true);
        llRefillGlass.setEnabled(true);
    }

    @Override
    public void onDishClick(TrendingDishModel itemModel) {
        if (Utils.isNetworkConnected(this)) {
            if (mViewModel.getShopPk() < 0 || mViewModel.isRequestedCheckout())
                return;
            SessionMenuActivity.Companion.startWithSession(this, mViewModel.getShopPk(), null, itemModel.getPk());
        } else {
            Utils.toast(this, R.string.error_unavailable_network);
        }
    }
}
