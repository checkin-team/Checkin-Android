package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ShopInvoiceFeedbackAdapter extends RecyclerView.Adapter<ShopInvoiceFeedbackAdapter.ShopInvoiceFeedbackHolder>{
    @NonNull
    @Override
    public ShopInvoiceFeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceFeedbackHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ShopInvoiceFeedbackHolder extends RecyclerView.ViewHolder{
        public ShopInvoiceFeedbackHolder(View itemView) {
            super(itemView);
        }
    }
}
