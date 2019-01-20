package com.checkin.app.checkin.Shop.ShopInvoice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Util;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopInvoiceAdapter extends RecyclerView.Adapter<ShopInvoiceAdapter.ShopInvoiceHolder> {

    private List<RestaurantSessionModel> data;

    ShopInvoiceAdapter() {
    }

    @NonNull
    @Override
    public ShopInvoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice, parent, false);
        return new ShopInvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceHolder holder, int position) {
        RestaurantSessionModel mData = data.get(position);
        holder.bindData(mData);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    void addSessionData(List<RestaurantSessionModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ShopInvoiceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_hash_id)
        TextView tvHashId;
        @BindView(R.id.tv_shop_invoice_date)
        TextView tvShopInvoiceDate;
        @BindView(R.id.tv_shop_invoice_waiter_name)
        TextView tvShopInvoiceWaiterName;
        @BindView(R.id.tv_shop_invoice_item)
        TextView tvShopInvoiceItem;
        @BindView(R.id.tv_shop_invoice_rupee)
        TextView tvShopInvoiceRupee;
        @BindView(R.id.tv_shop_invoice_user_number)
        TextView tvShopInvoiceUserNumber;
        @BindView(R.id.tv_shop_invoice_table_number)
        TextView tvShopInvoiceTableNumber;
        @BindView(R.id.cv_shop_invoice)
        CardView cvShopInvoice;

        ShopInvoiceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(RestaurantSessionModel data) {
            //Date mDate = data.getCheckedIn();
            Date mDate = data.getCheckedOut();
            Integer mCountCustomer = data.getCountCustomers();
            Integer mCountOrder = data.getCountOrders();
            String mHashId = data.getHashId();
            String mTable = data.getTable();
            String mTotal = data.getTotal();

            BriefModel mHost = data.getHost();

            if (mHost != null){
                tvShopInvoiceWaiterName.setText(mHost.getDisplayName());
            }else {
                tvShopInvoiceWaiterName.setText("Unassigned");
            }

            tvHashId.setText(mHashId);
            tvShopInvoiceRupee.setText(mTotal);

            try{
                String mFinalDate = data.getFormattedDate(mDate);
                tvShopInvoiceDate.setText(mFinalDate);
            }catch (Exception ex){
                Log.d("Parse Exception",ex.getLocalizedMessage());
            }
            tvShopInvoiceUserNumber.setText(String.valueOf(mCountCustomer));
            tvShopInvoiceItem.setText(String.format("%s %s",mCountOrder,"Item's"));
            tvShopInvoiceTableNumber.setText(mTable);

            cvShopInvoice.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),ShopInvoiceDetailFeedbackActivity.class);
                intent.putExtra(ShopInvoiceDetailFeedbackActivity.ORDER_DETAIL,data);
                view.getContext().startActivity(intent);
            });
        }
    }
}
