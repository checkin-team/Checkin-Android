package com.checkin.app.checkin.Cook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Cook.Fragment.CookSessionOrderFragment;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.SessionBriefModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_END;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_NEW_ORDER;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_SWITCH_TABLE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_UPDATE_ORDER;

public class CookSessionActivity extends AppCompatActivity {
    public static final String KEY_SESSION_PK = "cook.session.session_pk";
    public static final String KEY_SHOP_PK = "cook.session.shop_pk";

    private static final String TAG = CookSessionActivity.class.getSimpleName();

    @BindView(R.id.toolbar_cook_session)
    Toolbar toolbar;
    @BindView(R.id.tv_cook_session_waiter)
    TextView tvWaiterName;
    @BindView(R.id.tv_cook_session_table)
    TextView tvTable;
    @BindView(R.id.im_cook_session_waiter)
    ImageView imWaiterPic;

    private CookSessionOrderFragment mOrderFragment;
    private CookSessionViewModel mViewModel;

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
                case COOK_SESSION_NEW_ORDER:
                    orderedItemModel = message.getRawData().getSessionOrderedItem();
                    CookSessionActivity.this.addNewOrder(orderedItemModel);
                    break;
                case COOK_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    CookSessionActivity.this.updateSessionHost(user);
                    break;
                case COOK_SESSION_UPDATE_ORDER:
                    long orderPk = message.getRawData().getSessionOrderId();
                    CookSessionActivity.this.updateOrderStatus(orderPk, message.getRawData().getSessionEventStatus());
                    break;
                case COOK_SESSION_END:
                    finish();
                    break;
                case COOK_SESSION_SWITCH_TABLE:
                    tvTable.setText(message.getRawData().getSessionNewTable());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(CookSessionViewModel.class);
        mOrderFragment = CookSessionOrderFragment.newInstance();

        setupUi();
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        long sessionId = getIntent().getLongExtra(KEY_SESSION_PK, 0L);
        long shopId = getIntent().getLongExtra(KEY_SHOP_PK, 0L);
        mViewModel.fetchSessionBriefData(sessionId);
        mViewModel.setShopPk(shopId);
        mViewModel.fetchSessionOrders();

        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.getData();
            switch (resource.getStatus()) {
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
                    Utils.toast(this, resource.getMessage());
            }
        });

        setupOrdersListing();
    }

    private void setupData(SessionBriefModel data) {
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

    private void updateOrderStatus(long orderPk, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        OrderStatusModel data = new OrderStatusModel(orderPk, statusType);
        mViewModel.updateUiOrderStatus(data);
    }

    private void addNewOrder(SessionOrderedItemModel orderedItemModel) {
        mViewModel.addOrderData(orderedItemModel);
    }

    // endregion

    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_manager_session_fragment, mOrderFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[]{
                COOK_SESSION_NEW_ORDER, COOK_SESSION_HOST_ASSIGNED, COOK_SESSION_UPDATE_ORDER,
                COOK_SESSION_END, COOK_SESSION_SWITCH_TABLE
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
        mViewModel.fetchSessionBriefData();
        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());
        mViewModel.fetchSessionOrders();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    @OnClick(R.id.im_cook_session_back)
    public void goBack() {
        onBackPressed();
    }
}
