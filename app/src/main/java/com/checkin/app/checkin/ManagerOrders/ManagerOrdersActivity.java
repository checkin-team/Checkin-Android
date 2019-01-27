package com.checkin.app.checkin.ManagerOrders;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerOrdersActivity extends AppCompatActivity implements ManagerOrdersFragment.ManagerOrdersInteraction {
    @BindView(R.id.im_swipe_up)
    ImageView imSwipeUp;
    @BindView(R.id.tv_count_orders_new)
    TextView tvCountOrdersNew;
    @BindView(R.id.tv_count_orders_in_progress)
    TextView tvCountOrdersInProgress;
    @BindView(R.id.tv_count_orders_delivered)
    TextView tvCountOrdersDelivered;
    @BindView(R.id.tv_cart_item_price)
    TextView tvCartItemPrice;
    @BindView(R.id.tv_waiter_name)
    TextView tvWaiterName;
    @BindView(R.id.im_as_waiter_pic)
    ImageView imWaiterPic;
    private ManagerOrdersViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_orders);
        ButterKnife.bind(this);
        setData();
    }

    private void setData() {
        mViewModel = ViewModelProviders.of(this).get(ManagerOrdersViewModel.class);
        mViewModel.fetchManagerOrdersBrief(3);
        mViewModel.fetchManagerOrdersDetails(3);

        mViewModel.getManagerOrdersBriefData().observe(this,activeSessionModelResource -> {
            if (activeSessionModelResource == null) return;
            ActiveSessionModel data = activeSessionModelResource.data;
            switch (activeSessionModelResource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    tvCartItemPrice.setText(String.format(
                            Locale.ENGLISH, Utils.getCurrencyFormat(this), data.getBill()));
                    if (data.gethost() != null) {
                        tvWaiterName.setText(data.gethost().getDisplayName());
                        Utils.loadImageOrDefault(imWaiterPic, data.gethost().getDisplayPic(), R.drawable.ic_waiter);
                    } else {
                        tvWaiterName.setText(R.string.waiter_unassigned);
                    }
//                    mViewModel.setSessionPk(data.getPk());
//                    mViewModel.setShopPk(data.getShopPk());
//                    if (mViewModel.getSessionPk() != data.getPk()) {
//                        showPostCheckIn();
//                    }
//                    setupData(data);
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(activeSessionModelResource.status.name(), activeSessionModelResource.message == null ? "Null" : activeSessionModelResource.message);
                }
            }
        });


        mViewModel.getNewItemCount().observe(this,integer -> {
            tvCountOrdersNew.setText(String.valueOf(integer));
        });
        mViewModel.getInProgressItemCount().observe(this,integer -> {
            tvCountOrdersInProgress.setText(String.valueOf(integer));
        });
        mViewModel.getDeliveredItemCount().observe(this,integer -> {
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

    @OnClick(R.id.im_swipe_up)
    public void onSwipeUp() {
        setupOrdersListing();
    }

    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_orders_fragment, ManagerOrdersFragment.newInstance(this), "manager_orders")
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag("manager_orders");
        if (mFragment != null) {
            ((ManagerOrdersFragment) mFragment).onBackPressed();
        } else super.onBackPressed();
    }

    @OnClick(R.id.btn_back)
    public void goBack(View v) {
        onBackPressed();
    }
}
