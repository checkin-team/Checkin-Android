package com.checkin.app.checkin.Shop.ShopInvoice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

public class ShopInvoiceAdapter extends RecyclerView.Adapter<ShopInvoiceAdapter.ShopInvoiceHolder>{

    private ShopInvoiceActivity shopInvoiceActivity;

    ShopInvoiceAdapter(ShopInvoiceActivity shopInvoiceActivity) {
        this.shopInvoiceActivity = shopInvoiceActivity;
    }

    @NonNull
    @Override
    public ShopInvoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice,parent,false);
        return new ShopInvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceHolder holder, int position) {
        holder.cv_shop_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartDetailFeedbackActivity(shopInvoiceActivity);
            }
        });
    }

    private void mStartDetailFeedbackActivity(ShopInvoiceActivity shopInvoiceActivity) {
        Intent intent = new Intent(shopInvoiceActivity, ShopInvoiceDetailFeedbackActivity.class);
        shopInvoiceActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ShopInvoiceHolder extends RecyclerView.ViewHolder{
        CardView cv_shop_invoice;

        ShopInvoiceHolder(View itemView) {
            super(itemView);
            cv_shop_invoice = itemView.findViewById(R.id.cv_shop_invoice);
        }
    }
}
