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

public class ShopInvoiceDetailFragment extends Fragment {

    public static ShopInvoiceDetailFragment newInstance() {
        return new ShopInvoiceDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_invoice_detail,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv_shop_invoice_detail = view.findViewById(R.id.rv_shop_invoice_detail);
        ShopInvoiceDetailAdapter shopInvoiceDetailAdapter = new ShopInvoiceDetailAdapter();
        rv_shop_invoice_detail.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_shop_invoice_detail.setAdapter(shopInvoiceDetailAdapter);
    }
}
