package com.checkin.app.checkin.Manager.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceDetailActivity;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceSessionAdapter;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;

public class ManagerInvoiceFragment extends BaseFragment implements ShopInvoiceSessionAdapter.ShopInvoiceInteraction {

    @BindView(R.id.rv_shop_invoice_sessions)
    RecyclerView rvSessions;

    private ShopInvoiceSessionAdapter mAdapter;
    private ShopInvoiceViewModel mViewModel;

    public ManagerInvoiceFragment() {
    }

    public static ManagerInvoiceFragment newInstance() {
        return new ManagerInvoiceFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setupUi();
        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopInvoiceViewModel.class);

        mViewModel.getRestaurantSessions().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                mAdapter.setSessionData(input.data);
            } else if (input.status != Resource.Status.LOADING) {
                Utils.toast(requireContext(), input.message);
            }
        });
    }

    private void setupUi() {
        rvSessions.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mAdapter = new ShopInvoiceSessionAdapter(this);
        rvSessions.setAdapter(mAdapter);
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_manager_invoice;
    }

    @Override
    public void onClickSession(RestaurantSessionModel data) {
        Intent intent = new Intent(requireContext(), ShopInvoiceDetailActivity.class);
        intent.putExtra(ShopInvoiceDetailActivity.KEY_SESSION_DATA, data);
        startActivity(intent);
    }
}
