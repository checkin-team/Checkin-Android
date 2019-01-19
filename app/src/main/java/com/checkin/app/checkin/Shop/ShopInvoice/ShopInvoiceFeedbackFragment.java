package com.checkin.app.checkin.Shop.ShopInvoice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopInvoiceFeedbackFragment extends Fragment {

    @BindView(R.id.rv_shop_invoice_feedback)
    RecyclerView rvShopInvoiceFeedback;
    Unbinder unbinder;

    public static ShopInvoiceFeedbackFragment newInstance() {
        return new ShopInvoiceFeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_invoice_feedback, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopInvoiceFeedbackAdapter shopInvoiceFeedbackAdapter = new ShopInvoiceFeedbackAdapter();
        rvShopInvoiceFeedback.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShopInvoiceFeedback.setAdapter(shopInvoiceFeedbackAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
