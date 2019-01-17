package com.checkin.app.checkin.Session.ActiveSession;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChat;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionCustomChatDataModel;
import com.checkin.app.checkin.Session.SelfPresenceModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionActivity extends AppCompatActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction {
    private static final String TAG = ActiveSessionActivity.class.getSimpleName();

    public static final String IDENTIFIER = "active_session";

    private static final int RC_SEARCH_MEMBER = 201;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;
//    private ActiveSessionRecommendations mSessionRecommendationAdapter;
    @BindView(R.id.rv_session_members) RecyclerView rvMembers;
    @BindView(R.id.rv_session_recommendations) RecyclerView rvRecommendations;
    @BindView(R.id.tv_active_session_bill) TextView tvBill;
    @BindView(R.id.tv_waiter_name) TextView tv_waiter_name;
    @BindView(R.id.im_waiter) CircleImageView im_waiter;
    @BindView(R.id.switch_session_presence) Switch switchSessionPresence;
    @BindView(R.id.ll_refill_glass) LinearLayout ll_refill_glass;
    @BindView(R.id.bottom_session_container) ConstraintLayout bottom_session_container;
    @BindView(R.id.tv_menu_count_ordered_items) TextView tvCountItems;

    private Receiver mHandler;
    private MenuViewModel mMenuViewModel;
    private  String session_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        String shopPk = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.SP_SESSION_RESTAURANT_PK, "3");

        rvMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSessionMembersAdapter = new ActiveSessionMemberAdapter(null, this);
        rvMembers.setAdapter(mSessionMembersAdapter);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.getActiveSessionDetail().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    mSessionMembersAdapter.setUsers(data != null ? data.getCustomers() : null);
                    tvBill.setText(data != null ? data.getBill() : null);
                    switchSessionPresence.setChecked(data.isIs_public());
                    if(data.gethost()!=null) {
                        tv_waiter_name.setText(data.gethost().getDisplayName());
                        Utils.loadImageOrDefault(im_waiter,data.gethost().getDisplayPic(),R.drawable.ic_waiter);
                    }else
                        tv_waiter_name.setText("Unassigned");

                    mViewModel.setSessionId(data.getPk());
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

//        mMenuViewModel.getTotalOrderedCount().observe(this, count -> {
//            if (count == null)
//                return;
//            if (count > 0)
//                tvCountItems.setText(Utils.formatCount(count));
//            else
//                tvCountItems.setText("");
//        });


        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    mViewModel.updateResults();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, resource.message);
                }
            }
        });

        switchSessionPresence.setOnClickListener(v -> {
            if(switchSessionPresence.isChecked()) mViewModel.sendSelfPresence(true);
            else mViewModel.sendSelfPresence(false);
        });
        mViewModel.setShopPk(shopPk);


        bottom_session_container.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                openChat(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_NONE);
            return true;
        });

    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(this, "", mViewModel.getShopPk());
    }

    @OnClick(R.id.btn_active_session_orders)
    public void onViewOrders() {
        startActivity(new Intent(this, ActiveSessionViewOrdersActivity.class));
    }

    /*@OnClick(R.id.btn_active_session_checkout)
    public void onCheckout() {
        new AlertDialog.Builder(this)
                .setTitle("Checkout Session")
                .setMessage("Request for bill?")
                .setPositiveButton("Confirm", (dialogInterface, i) -> mViewModel.checkoutSession())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }*/

    @OnClick(R.id.btn_cart)
    public void openBillDetails(){
        startActivity(new Intent(this, ActiveSessionInvoiceActivity.class).putExtra("session_id",mViewModel.getSessionId()));
    }

    @OnClick(R.id.ll_call_waiter_container)
    public void openChatCallWaiter(){
        openChat(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CALL_WAITER);
    }
    @OnClick(R.id.ll_table_cleaning_container)
    public void openChatCleanContainer(){
        openChat(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CLEAN_TABLE);
    }
    @OnClick(R.id.ll_refill_glass)
    public void openChatRefillGlass(){
        openChat(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
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
            String userPk = data.getStringExtra(SearchActivity.KEY_RESULT_PK);
            mViewModel.addMembers(userPk);
        }
    }
    public void openChat(ActiveSessionCustomChatDataModel.CHATSERVICETYPES serviceTypes){
        Intent myIntent = new Intent(ActiveSessionActivity.this, ActiveSessionChat.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(ActiveSessionActivity.this, R.anim.slide_up, R.anim.slide_down);
        myIntent.putExtra("service",serviceTypes);
        startActivity(myIntent, options.toBundle());
    }

    /*private void setUpRecommendedMenu(){
        rvRecommendations.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mSessionRecommendationAdapter = new ActiveSessionRecommendations(null);
        rvRecommendations.setAdapter(mSessionRecommendationAdapter);


    }*/

    private static class Receiver extends BroadcastReceiver {
        ActiveSessionViewModel mSessionViewModel;

        Receiver(ActiveSessionViewModel viewModel) {
            mSessionViewModel = viewModel;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mSessionViewModel.updateResults();
        }
    }
}
