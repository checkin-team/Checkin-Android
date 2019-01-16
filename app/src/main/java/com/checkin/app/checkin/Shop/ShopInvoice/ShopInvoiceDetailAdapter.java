package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

public class ShopInvoiceDetailAdapter extends RecyclerView.Adapter<ShopInvoiceDetailAdapter.ShopInvoiceDetailHolder>{
    @NonNull
    @Override
    public ShopInvoiceDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice_detail,parent,false);
        return new ShopInvoiceDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceDetailHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ShopInvoiceDetailHolder extends RecyclerView.ViewHolder{
        ShopInvoiceDetailHolder(View itemView) {
            super(itemView);
        }
    }
}
