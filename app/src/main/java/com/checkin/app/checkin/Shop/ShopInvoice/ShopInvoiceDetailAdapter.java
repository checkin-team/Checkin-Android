package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopInvoiceDetailAdapter extends RecyclerView.Adapter<ShopInvoiceDetailAdapter.ShopInvoiceDetailHolder> {

    private List<ShopSessionDetailModel.OrderedItem> mList;

    @NonNull
    @Override
    public ShopInvoiceDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice_detail, parent, false);
        return new ShopInvoiceDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceDetailHolder holder, int position) {
        ShopSessionDetailModel.OrderedItem mItem = mList.get(position);
        holder.bindData(mItem);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    void addSessionDetailData(List<ShopSessionDetailModel.OrderedItem> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class ShopInvoiceDetailHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_shop_invoice_detail)
        ImageView ivShopInvoiceDetail;
        @BindView(R.id.tv_shop_invoice_detail)
        TextView tvShopInvoiceDetail;
        @BindView(R.id.tv_shop_invoice_detail_rupee)
        TextView tvShopInvoiceDetailRupee;

        ShopInvoiceDetailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(ShopSessionDetailModel.OrderedItem mItem) {
            Boolean isVeg = mItem.getItem().getIsVegetarian();
            String mName = mItem.getItem().getName();
            String mCost = mItem.getCost();

            if (isVeg){
                ivShopInvoiceDetail.setImageResource(R.drawable.ic_veg);
            }else {
                ivShopInvoiceDetail.setImageResource(R.drawable.ic_nonveg);
            }

            tvShopInvoiceDetail.setText(mName);
            tvShopInvoiceDetailRupee.setText(mCost);
        }
    }
}
