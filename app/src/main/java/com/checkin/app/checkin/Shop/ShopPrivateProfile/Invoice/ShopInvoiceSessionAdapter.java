package com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopInvoiceSessionAdapter extends RecyclerView.Adapter<ShopInvoiceSessionAdapter.ShopInvoiceHolder> {
    private List<RestaurantSessionModel> mData;
    private ShopInvoiceInteraction mListener;

    ShopInvoiceSessionAdapter(ShopInvoiceInteraction listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopInvoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_invoice_session, parent, false);
        return new ShopInvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    void setSessionData(List<RestaurantSessionModel> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class ShopInvoiceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_invoice_session_id)
        TextView tvSessionId;
        @BindView(R.id.tv_invoice_session_date)
        TextView tvDate;
        @BindView(R.id.tv_invoice_session_bill)
        TextView tvBill;
        @BindView(R.id.tv_invoice_session_item_count)
        TextView tvItemCount;
        @BindView(R.id.tv_invoice_session_member_count)
        TextView tvMemberCount;
        @BindView(R.id.tv_invoice_session_waiter)
        TextView tvWaiter;
        @BindView(R.id.tv_invoice_session_table)
        TextView tvTable;

        private RestaurantSessionModel mData;

        ShopInvoiceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> mListener.onClickSession(mData));
        }

        public void bindData(RestaurantSessionModel data) {
            mData = data;

            BriefModel host = data.getHost();
            tvWaiter.setText(String.format(Locale.ENGLISH, "Waiter : %s", host != null ? host.getDisplayName() : itemView.getResources().getString(R.string.waiter_unassigned)));

            tvSessionId.setText(data.getHashId());
            tvBill.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(itemView.getContext()), data.formatTotal()));
            tvDate.setText(data.getFormattedDate());
            tvMemberCount.setText(String.valueOf(data.getCountCustomers()));
            tvItemCount.setText(String.format(Locale.ENGLISH, " | %d item(s)", data.getCountOrders()));
            tvTable.setText(data.getTable());
        }
    }

    public interface ShopInvoiceInteraction {
        void onClickSession(RestaurantSessionModel data);
    }
}
