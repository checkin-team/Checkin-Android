package com.checkin.app.checkin.Session.ActiveSession;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.OnBoardingUtils;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionActivity extends BaseActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction {
    private static final int RC_SEARCH_MEMBER = 201;

    @BindView(R.id.rv_session_members)
    RecyclerView rvMembers;
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

    @BindView(R.id.tv_as_order_new_count)
    TextView tvCountOrdersNew;
    @BindView(R.id.tv_as_order_progress_count)
    TextView tvCountOrdersInProgress;
    @BindView(R.id.tv_as_order_done_count)
    TextView tvCountOrdersDelivered;
    @BindView(R.id.tv_session_checkout)
    TextView tvSessionCheckout;
    @BindView(R.id.btn_active_session_menu)
    TextView btnSessionMenu;
    @BindView(R.id.rl_container_session_orders)
    RelativeLayout rlSessionOrders;
    @BindView(R.id.ll_call_waiter_button)
    LinearLayout llCallWaiter;
    @BindView(R.id.ll_table_cleaning_button)
    LinearLayout llTableCleaning;
    @BindView(R.id.ll_refill_glass_button)
    LinearLayout llRefillGlass;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;
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
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    stopRefreshing();
                    mViewModel.setSessionPk(data.getPk());
                    mViewModel.setShopPk(data.getShopPk());
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
                    Utils.toast(this, resource.message);
                    break;
                }
            }
        });

        mViewModel.getSessionMemberUpdate().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
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
                    Utils.toast(this, resource.message);
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
            tvCountOrdersInProgress.setText(String.valueOf(integer));
        });
        mViewModel.getCountDeliveredOrders().observe(this, integer -> {
            if (integer == null)
                integer = 0;
            tvCountOrdersDelivered.setText(String.valueOf(integer));
        });
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
        OnBoardingUtils.animateOnBoarding(this, new OnBoardingUtils.OnBoardingModel("Interact with waiter here!", containerBottomActions));
    }

    private void setupData(ActiveSessionModel data) {
        mSessionMembersAdapter.setUsers(data.getCustomers());
        tvBill.setText(data.formatBill(this));
        tvBill.setEnabled(true);
        tvSessionLiveAt.setText(data.getRestaurant().getDisplayName());
        if (data.getHost() != null) {
            tvWaiterName.setText(data.getHost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
        if (!data.isRequestedCheckout()) {
            btnSessionMenu.setEnabled(true);
            rlSessionOrders.setEnabled(true);
            rlSessionOrders.setVisibility(View.VISIBLE);
        } else {
            btnSessionMenu.setEnabled(false);
            tvSessionCheckout.setVisibility(View.VISIBLE);
            rlSessionOrders.setVisibility(View.GONE);
        }

    }

    // region UI-Update

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

    private void addNewOrder(SessionOrderedItemModel sessionOrderedItem) {
        mViewModel.addNewOrder(sessionOrderedItem);
    }

    // endregion

    // region Click Listeners

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        if (mViewModel.getShopPk() > 0)
            SessionMenuActivity.withSession(this, mViewModel.getShopPk(), null);
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

    @OnClick(R.id.tv_active_session_bill)
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
//        ActivityOptions options = ActivityOptions.makeCustomAnimation(ActiveSessionActivity.this, R.anim.slide_up, R.anim.slide_down);
        myIntent.putExtra(SessionChatActivity.KEY_SERVICE_TYPE, service.tag);
        startActivity(myIntent);
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
}
