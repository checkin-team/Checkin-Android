package com.checkin.app.checkin.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.manager.fragment.ManagerSessionEventFragment;
import com.checkin.app.checkin.manager.fragment.ManagerSessionOrderFragment;
import com.checkin.app.checkin.manager.model.ManagerSessionEventModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.SessionBriefModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_BILL_CHANGE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_UPDATE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_MEMBER_CHANGE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_UPDATE_ORDER;

public class ManagerSessionActivity extends AppCompatActivity implements
        ManagerSessionOrderFragment.ManagerOrdersInteraction {
    public static final String KEY_SESSION_PK = "manager.session.session_pk";
    public static final String KEY_SHOP_PK = "manager.session.shop_pk";
    public static final String KEY_OPEN_ORDERS = "manager.session.open_orders";
    private static final String TAG = ManagerSessionActivity.class.getSimpleName();
    @BindView(R.id.tv_ms_order_new_count)
    TextView tvCountOrdersNew;
    @BindView(R.id.tv_ms_order_progress_count)
    TextView tvCountOrdersInProgress;
    @BindView(R.id.tv_ms_order_done_count)
    TextView tvCountOrdersDelivered;
    @BindView(R.id.tv_manager_session_bill)
    TextView tvCartItemPrice;
    @BindView(R.id.tv_manager_session_waiter)
    TextView tvWaiterName;
    @BindView(R.id.tv_manager_session_table)
    TextView tvTable;
    @BindView(R.id.im_manager_session_waiter)
    ImageView imWaiterPic;
    @BindView(R.id.container_ms_order_new)
    LinearLayout containerMsOrderNew;
    @BindView(R.id.container_ms_order_progress)
    LinearLayout containerMsOrderProgress;
    @BindView(R.id.container_ms_order_done)
    LinearLayout containerMsOrderDone;

    private ManagerSessionOrderFragment mOrderFragment;
    private ManagerSessionEventFragment mEventFragment;
    private ManagerSessionViewModel mViewModel;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel session = message.getSessionDetail();
            if (session != null && session.getPk() != mViewModel.getSessionPk())
                return;

            BriefModel user;
            SessionOrderedItemModel orderedItemModel;
            switch (message.getType()) {
                case MANAGER_SESSION_NEW_ORDER:
                    orderedItemModel = message.getRawData().getSessionOrderedItem();
                    ManagerSessionActivity.this.addNewOrder(orderedItemModel);
                    break;
                case MANAGER_SESSION_EVENT_SERVICE:
                case MANAGER_SESSION_EVENT_CONCERN:
                case MANAGER_SESSION_CHECKOUT_REQUEST:
                    ManagerSessionActivity.this.addSessionEvent(message.getRawData().getSessionEventBrief());
                    break;
                case MANAGER_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    ManagerSessionActivity.this.updateSessionHost(user);
                    break;
                case MANAGER_SESSION_BILL_CHANGE:
                    ManagerSessionActivity.this.updateBill(message.getRawData().getSessionBillTotal());
                    break;
                case MANAGER_SESSION_MEMBER_CHANGE:
                    ManagerSessionActivity.this.updateMemberCount(message.getRawData().getSessionCustomerCount());
                    break;
               /* case MANAGER_SESSION_UPDATE_ORDER:
                    long orderPk = message.getRawData().getSessionOrderId();
                    ManagerSessionActivity.this.updateOrderStatus(orderPk, message.getRawData().getSessionEventStatus());
                    break;*/
                case MANAGER_SESSION_EVENT_UPDATE:
                    long eventPk = message.getObject().getPk();
                    ManagerSessionActivity.this.updateEventStatus(eventPk, message.getRawData().getSessionEventStatus());
                    break;
            }
        }
    };
    private SessionBriefModel mSessionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ManagerSessionViewModel.class);
        mOrderFragment = ManagerSessionOrderFragment.newInstance(this);
        mEventFragment = ManagerSessionEventFragment.newInstance();
        setupUi();
        setupEventListing();
    }

    private void setupUi() {
        long sessionId = getIntent().getLongExtra(KEY_SESSION_PK, 0L);
        long shopId = getIntent().getLongExtra(KEY_SHOP_PK, 0L);
        mViewModel.fetchSessionBriefData(sessionId);
        mViewModel.setShopPk(shopId);
        mViewModel.fetchSessionOrders();

        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.data;
            switch (resource.status) {
                case SUCCESS:
                    if (data == null)
                        return;
                    setupData(data);
                    break;
                case ERROR_NOT_FOUND:
                    finish();
                    break;
                case LOADING:
                    break;
                default:
                    Utils.toast(this, resource.message);
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

        boolean shouldOpenOrders = getIntent().getBooleanExtra(KEY_OPEN_ORDERS, false);
        if (shouldOpenOrders) setupOrdersListing();
    }

    private void setupData(SessionBriefModel data) {
        mSessionData = data;
        tvCartItemPrice.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(this), data.formatBill()));
        if (data.getHost() != null) {
            tvWaiterName.setText(data.getHost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            imWaiterPic.setImageResource(R.drawable.ic_waiter);
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
        tvTable.setText(data.getTable());
    }

    // region UI-Update

    private void updateSessionHost(BriefModel user) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setHost(user);
        }
        mViewModel.updateSessionData(data);
    }

    private void addSessionEvent(ManagerSessionEventModel eventModel) {
        mViewModel.addEventData(eventModel);
    }

    private void updateBill(double bill) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setBill(bill);
        }
        mViewModel.updateSessionData(data);
    }

    private void updateMemberCount(int count) {
        SessionBriefModel data = mViewModel.getSessionData();
        if (data != null) {
            data.setCustomerCount(count);
        }
        mViewModel.updateSessionData(data);
    }

    private void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        OrderStatusModel data = new OrderStatusModel(orderPk, statusType);
        mViewModel.updateUiOrderStatus(data);
    }

    private void addNewOrder(SessionOrderedItemModel orderedItemModel) {
        mViewModel.addOrderData(orderedItemModel);
    }

    private void updateEventStatus(long eventPk, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        mViewModel.updateUiEventStatus(eventPk, sessionEventStatus);
    }

    // endregion

    @OnClick(R.id.container_ms_bottom_actions)
    public void onSwipeUp() {
        setupOrdersListing();
    }

    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_manager_session_fragment, mOrderFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupEventListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_manager_session_events, mEventFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mOrderFragment != null && !mOrderFragment.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[]{
                MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN, MANAGER_SESSION_HOST_ASSIGNED, MANAGER_SESSION_BILL_CHANGE,
                MANAGER_SESSION_MEMBER_CHANGE, MANAGER_SESSION_UPDATE_ORDER, MANAGER_SESSION_EVENT_UPDATE
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
        mViewModel.fetchSessionBriefData();
        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    @OnClick(R.id.tv_manager_session_bill)
    public void onCheckSessionBill() {
        if (mSessionData == null) return;
        Intent intent = new Intent(this, ManagerSessionInvoiceActivity.class);
        intent.putExtra(ManagerSessionInvoiceActivity.TABLE_NAME, mSessionData.getTable())
                .putExtra(ManagerSessionInvoiceActivity.KEY_SESSION, mViewModel.getSessionPk())
                .putExtra(ManagerSessionInvoiceActivity.IS_REQUESTED_CHECKOUT, mSessionData.isRequestedCheckout());
        startActivity(intent);
    }

    @OnClick(R.id.im_manager_session_back)
    public void goBack() {
        onBackPressed();
    }

    @OnClick({R.id.container_ms_order_new, R.id.container_ms_order_progress, R.id.container_ms_order_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.container_ms_order_new:
            case R.id.container_ms_order_progress:
            case R.id.container_ms_order_done:
                setupOrdersListing();
                break;
        }
    }

    @Override
    public void onGenerateBillClick() {
        if (mSessionData == null) return;
        Intent intent = new Intent(this, ManagerSessionInvoiceActivity.class);
        intent.putExtra(ManagerSessionInvoiceActivity.TABLE_NAME, mSessionData.getTable())
                .putExtra(ManagerSessionInvoiceActivity.KEY_SESSION, mViewModel.getSessionPk())
                .putExtra(ManagerSessionInvoiceActivity.IS_REQUESTED_CHECKOUT, mSessionData.isRequestedCheckout());
        startActivity(intent);
    }
}
