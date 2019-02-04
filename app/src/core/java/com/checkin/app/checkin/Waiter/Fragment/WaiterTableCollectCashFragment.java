package com.checkin.app.checkin.Waiter.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaiterTableCollectCashFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.tv_waiter_table_bill)
    TextView tvWaiterTableBill;

    private WaiterTableViewModel mViewModel;

    public static WaiterTableCollectCashFragment newInstance() {
        return new WaiterTableCollectCashFragment();
    }

    public WaiterTableCollectCashFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_waiter_table_collect_cash, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment fragment = getParentFragment();
        if (fragment == null)
            return;

        mViewModel = ViewModelProviders.of(fragment).get(WaiterTableViewModel.class);
        mViewModel.getSessionDetail().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null)
                tvWaiterTableBill.setText(Utils.formatCurrencyAmount(requireContext(), resource.data.getBill()));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
