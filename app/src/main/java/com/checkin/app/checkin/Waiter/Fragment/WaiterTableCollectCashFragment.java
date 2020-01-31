package com.checkin.app.checkin.Waiter.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaiterTableCollectCashFragment extends Fragment {
    @BindView(R.id.title_waiter_collect)
    TextView titleWaiterCollect;
    @BindView(R.id.tv_waiter_table_bill)
    TextView tvWaiterTableBill;
    @BindView(R.id.tv_waiter_table_payment_mode)
    TextView tvWaiterTablePaymentMode;
    private Unbinder unbinder;
    private WaiterTableViewModel mViewModel;

    public WaiterTableCollectCashFragment() {
    }

    public static WaiterTableCollectCashFragment newInstance() {
        return new WaiterTableCollectCashFragment();
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
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                tvWaiterTableBill.setText(Utils.formatCurrencyAmount(requireContext(), resource.getData().getBill()));
                if (resource.getData().getPaymentModes() == null) {
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                    }
                }else {
                    tvWaiterTablePaymentMode.setText(ShopModel.getPaymentMode(resource.getData().getPaymentModes()));
                }
                tvWaiterTablePaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(resource.getData().getPaymentModes()), 0, 0, 0);
                if (tvWaiterTablePaymentMode.getText().toString().equalsIgnoreCase("via Cash")) {
                    titleWaiterCollect.setText("Collect");
                    tvWaiterTablePaymentMode.setCompoundDrawablePadding(10);
                } else {
                    titleWaiterCollect.setText("");
                    tvWaiterTablePaymentMode.setCompoundDrawablePadding(0);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
