package com.checkin.app.checkin.Session.ActiveSession;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;

public class ActiveSessionActivity extends AppCompatActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction, PostCheckinFragment.PostCheckinInteraction {
    private static final String TAG = ActiveSessionActivity.class.getSimpleName();

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

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;
    private PostCheckinFragment mPostCheckinFragment;

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
                case USER_SESSION_BILL_CHANGE:
                    ActiveSessionActivity.this.updateBill(message.getRawData().get("bill").asText());
                case USER_SESSION_HOST_ASSIGNED:
                    ActiveSessionActivity.this.updateHost();
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
        setupInitialState();
        setupObservers();
    }

    private void setupObservers() {
        mViewModel.getSessionData().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    if (mViewModel.getSessionPk() != data.getPk()) {
                        showPostCheckIn();
                    }
                    mViewModel.setSessionPk(data.getPk());
                    mViewModel.setShopPk(data.getShopPk());
                    setupData(data);
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Utils.toast(this, "Done!");
                    updateState(mViewModel.getShopPk(), mViewModel.getSessionPk());
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, resource.message);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUi() {
        rvMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSessionMembersAdapter = new ActiveSessionMemberAdapter(null, this);
        rvMembers.setAdapter(mSessionMembersAdapter);

        containerBottomActions.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE);
            return true;
        });
    }

    private void setupData(ActiveSessionModel data) {
        mSessionMembersAdapter.setUsers(data.getCustomers());
        tvBill.setText(data.getBill());
        tvSessionLiveAt.setText(data.getRestaurant().formatRestaurantName());
        if (data.gethost() != null) {
            tvWaiterName.setText(data.gethost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.gethost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
    }

    private void updateBill(String bill) {
        mViewModel.updateBill(bill);
    }

    private void updateHost() {

    }

    private void updateState(long shopPk, long sessionPk) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putLong(Constants.SP_SESSION_ACTIVE_PK, sessionPk)
                .putLong(Constants.SP_SESSION_RESTAURANT_PK, shopPk)
                .apply();
        if (mPostCheckinFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mPostCheckinFragment)
                    .commit();
            mPostCheckinFragment = null;
        }
    }

    private void showPostCheckIn() {
        mPostCheckinFragment = PostCheckinFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_session, mPostCheckinFragment)
                .commit();
    }

    private void setupInitialState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mViewModel.setSessionPk(prefs.getLong(Constants.SP_SESSION_ACTIVE_PK, -1));
        mViewModel.fetchActiveSessionDetail();
    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(this, mViewModel.getShopPk(), null);
    }

    @OnClick(R.id.btn_active_session_orders)
    public void onViewOrders() {
        startActivity(new Intent(this, ActiveSessionViewOrdersActivity.class));
    }

    @OnClick(R.id.btn_active_session_invoice)
    public void openBillDetails() {
        startActivity(new Intent(
                this, ActiveSessionInvoiceActivity.class)
                .putExtra(ActiveSessionInvoiceActivity.KEY_SESSION_PK, mViewModel.getSessionPk()));
    }

    @OnClick(R.id.ll_call_waiter_button)
    public void openChatCallWaiter() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER);
    }

    @OnClick(R.id.ll_table_cleaning_button)
    public void openChatCleanContainer() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE);
    }

    @OnClick(R.id.ll_refill_glass_button)
    public void openChatRefillGlass() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @Override
    public void onAddMemberClicked(SessionCustomerModel customerModel) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure, you want to accept the request?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
               mViewModel.postSessionMember(customerModel.getPk());
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                mViewModel.deleteSessionMember(customerModel.getPk());

            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SEARCH_MEMBER && resultCode == RESULT_OK) {
            long userPk = data.getLongExtra(SearchActivity.KEY_RESULT_PK, 0L);
            mViewModel.addMembers(userPk);
        }
    }

    public void openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE service) {
        Intent myIntent = new Intent(ActiveSessionActivity.this, SessionChatActivity.class);
//        ActivityOptions options = ActivityOptions.makeCustomAnimation(ActiveSessionActivity.this, R.anim.slide_up, R.anim.slide_down);
        myIntent.putExtra(SessionChatActivity.KEY_SERVICE_TYPE, service.tag);
        startActivity(myIntent);
    }

    @Override
    public void OnProceedClicked(boolean isPublic) {
        mViewModel.sendSelfPresence(isPublic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageUtils.registerLocalReceiver(
                this, mReceiver, MESSAGE_TYPE.USER_SESSION_BILL_CHANGE, MESSAGE_TYPE.USER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.USER_SESSION_MEMBER_ADD_REQUEST, MESSAGE_TYPE.USER_SESSION_END);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }
}
