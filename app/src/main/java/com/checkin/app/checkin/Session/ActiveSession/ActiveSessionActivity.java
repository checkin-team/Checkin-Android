package com.checkin.app.checkin.Session.ActiveSession;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Util;

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

    private Receiver mHandler;

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
        getActiveSessionUsers();


        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    getActiveSessionUsers();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Util.toast(this, resource.message);
                }
            }
        });
        switchSessionPresence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

            } else {

            }
        });
        mViewModel.setShopPk(shopPk);


        mHandler = new Receiver(mViewModel);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mHandler, new IntentFilter(Util.getActivityIntentFilter(getApplicationContext(), IDENTIFIER)));
    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(this, "", mViewModel.getShopPk());
    }

    @OnClick(R.id.btn_active_session_checkout)
    public void onCheckout() {
        new AlertDialog.Builder(this)
                .setTitle("Checkout Session")
                .setMessage("Request for bill?")
                .setPositiveButton("Confirm", (dialogInterface, i) -> mViewModel.checkoutSession())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
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

    private void getActiveSessionUsers(){
        mViewModel.getActiveSessionDetail().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    mSessionMembersAdapter.setUsers(data != null ? data.getCustomers() : null);
                    tvBill.setText(data != null ? String.valueOf(data.getBill()) : null);

                    if(data.gethost()!=null) {
                        tv_waiter_name.setText(data.gethost().getDisplayName());
                        Util.loadImageOrDefault(im_waiter,data.gethost().getDisplayPic(),R.drawable.ic_waiter);
                    }else
                        tv_waiter_name.setText("Unassigned");
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });
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
