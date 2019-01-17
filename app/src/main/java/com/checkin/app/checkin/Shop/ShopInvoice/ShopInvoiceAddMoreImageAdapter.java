package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.OnClick;

public class ShopInvoiceAddMoreImageAdapter extends RecyclerView.Adapter<ShopInvoiceAddMoreImageAdapter.ShopInvoiceAddMoreImageHolder> {
    @NonNull
    @Override
    public ShopInvoiceAddMoreImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice_feedback_add_more, parent, false);
        return new ShopInvoiceAddMoreImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceAddMoreImageHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @OnClick(R.id.iv_shop_invoice_feedback)
    public void onViewClicked() {
    }

    class ShopInvoiceAddMoreImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_shop_invoice_feedback)
        ImageView ivShopInvoiceFeedback;

        ShopInvoiceAddMoreImageHolder(View itemView) {
            super(itemView);
        }
    }
}
