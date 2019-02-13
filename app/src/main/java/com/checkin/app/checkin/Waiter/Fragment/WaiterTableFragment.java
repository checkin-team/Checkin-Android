package com.checkin.app.checkin.Waiter.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;
import com.checkin.app.checkin.Waiter.WaiterWorkViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;

public class WaiterTableFragment extends BaseFragment {
    private static final String KEY_WAITER_TABLE_ID = "waiter.table";

    @BindView(R.id.container_waiter_table_actions)
    ViewGroup containerActions;
    @BindView(R.id.tv_waiter_table_members_count)
    TextView tvMembersCount;

    private WaiterTableInteraction mListener;
    private WaiterTableViewModel mViewModel;

    private long shopPk;

    public static WaiterTableFragment newInstance(long tableNumber, WaiterTableInteraction listener) {
        WaiterTableFragment fragment = new WaiterTableFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_WAITER_TABLE_ID, tableNumber);
        fragment.setArguments(bundle);
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_waiter_table;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null)
            return;

        shopPk = ViewModelProviders.of(requireActivity()).get(WaiterWorkViewModel.class).getShopPk();

        mViewModel = ViewModelProviders.of(this).get(WaiterTableViewModel.class);
        mViewModel.fetchSessionDetail(getArguments().getLong(KEY_WAITER_TABLE_ID, 0));

        mViewModel.getSessionDetail().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                setupTableData(resource.data);
            }
        });
        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                Utils.toast(requireContext(), "Session requested to end.");
            } else if (resource.status != Status.LOADING && resource.message != null) {
                Utils.toast(requireContext(), resource.message);
            }
        });
    }

    private void setupTableData(SessionBriefModel data) {
        tvMembersCount.setText(data.formatCustomerCount());
        if (data.isRequestedCheckout()) {
            containerActions.setVisibility(View.GONE);
            showCollectBill();
        } else {
            containerActions.setVisibility(View.VISIBLE);
            showEventList();
        }
    }

    private void showEventList() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableEventFragment.newInstance())
                .commit();
    }

    private void showCollectBill() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableCollectCashFragment.newInstance())
                .commit();
    }

    @Override
    public void updateScreen() {
        mViewModel.updateResults();
    }

    @OnClick(R.id.btn_waiter_table_checkout)
    public void onClickCheckout() {
        mViewModel.requestSessionCheckout();
    }

    @OnClick(R.id.btn_waiter_table_menu)
    public void onClickMenu() {
        SessionMenuActivity.withSession(requireContext(), shopPk, mViewModel.getSessionPk());
    }

    public WaiterTableViewModel getViewModel() {
        return mViewModel;
    }

    public interface WaiterTableInteraction {
    }
}
