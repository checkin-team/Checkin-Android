package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerSessionActivity extends AppCompatActivity implements ManagerSessionOrderFragment.ManagerOrdersInteraction {
    public static final String KEY_SESSION_PK = "manager.session_pk";

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

    private ManagerSessionOrderFragment mOrderFragment;
    private ManagerSessionViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ManagerSessionViewModel.class);
        setupUi();
        mOrderFragment = ManagerSessionOrderFragment.newInstance(this);
    }

    private void setupUi() {
        long sessionId = getIntent().getLongExtra(KEY_SESSION_PK, 0);
        mViewModel.fetchSessionBriefData(sessionId);
        mViewModel.fetchSessionOrders();

        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
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

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
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

    private void setupData(SessionBriefModel data) {
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

    @OnClick(R.id.im_ms_bottom_swipe_up)
    public void onSwipeUp() {
        setupOrdersListing();
    }

    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_manager_session_fragment, mOrderFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mOrderFragment != null && !mOrderFragment.onBackPressed())
            super.onBackPressed();
    }

    @OnClick(R.id.im_manager_session_back)
    public void goBack() {
        onBackPressed();
    }
}
