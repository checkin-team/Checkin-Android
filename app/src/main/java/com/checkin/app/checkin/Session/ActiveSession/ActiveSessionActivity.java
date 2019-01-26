package com.checkin.app.checkin.Session.ActiveSession;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionActivity extends AppCompatActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction, PostCheckinFragment.PostCheckinInteraction {
    private static final String TAG = ActiveSessionActivity.class.getSimpleName();

    private static final int RC_SEARCH_MEMBER = 201;

    @BindView(R.id.rv_session_members)
    RecyclerView rvMembers;
    @BindView(R.id.tv_active_session_bill)
    TextView tvBill;
    @BindView(R.id.tv_as_waiter_name)
    TextView tvWaiterName;
    @BindView(R.id.im_as_waiter_pic)
    ImageView imWaiterPic;
    @BindView(R.id.switch_session_presence)
    Switch switchSessionPresence;
    @BindView(R.id.container_as_actions_bottom)
    ViewGroup containerBottomActions;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);

        setupUi();
        setupInitialState();

        mViewModel.getSessionData().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    mViewModel.setSessionPk(data.getPk());
                    mViewModel.setShopPk(data.getShopPk());
                    if (mViewModel.getSessionPk() != data.getPk()) {
                        showPostCheckIn();
                    }
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
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
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

        switchSessionPresence.setOnClickListener(v -> {
            if (switchSessionPresence.isChecked()) mViewModel.sendSelfPresence(true);
            else mViewModel.sendSelfPresence(false);
        });
    }

    private void setupData(ActiveSessionModel data) {
        mSessionMembersAdapter.setUsers(data.getCustomers());
        tvBill.setText(data.getBill());
        switchSessionPresence.setChecked(data.isCheckinPublic());
        if (data.gethost() != null) {
            tvWaiterName.setText(data.gethost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.gethost().getDisplayPic(), R.drawable.ic_waiter_person);
        } else {
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
    }

    private void updateState(int shopPk, int sessionPk) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(Constants.SP_SESSION_ACTIVE_PK, sessionPk)
                .putInt(Constants.SP_SESSION_RESTAURANT_PK, shopPk)
                .apply();
    }

    private void showPostCheckIn() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_session, PostCheckinFragment.newInstance(this))
                .commit();
    }

    private void setupInitialState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mViewModel.setSessionPk(prefs.getInt(Constants.SP_SESSION_ACTIVE_PK, -1));
        mViewModel.fetchActiveSessionDetail();
    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(this, mViewModel.getSessionPk(), mViewModel.getShopPk());
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
    public void onAddMemberClicked() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.KEY_SEARCH_TYPE, SearchActivity.TYPE_PEOPLE);
        intent.putExtra(SearchActivity.KEY_SEARCH_MODE, SearchActivity.MODE_SELECT);
        startActivityForResult(intent, RC_SEARCH_MEMBER);
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
}
