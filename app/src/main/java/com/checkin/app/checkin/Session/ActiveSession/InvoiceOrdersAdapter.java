package com.checkin.app.checkin.Session.ActiveSession;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceOrdersAdapter extends RecyclerView.Adapter<InvoiceOrdersAdapter.ViewHolder> {

    private List<SessionOrderedItemModel> mOrderedItems;

    InvoiceOrdersAdapter(List<SessionOrderedItemModel> orderedItems) {
        this.mOrderedItems = orderedItems;
    }

    public void setData(List<SessionOrderedItemModel> data) {
        this.mOrderedItems = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_invoice_ordered_item;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mOrderedItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrderedItems != null ? mOrderedItems.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ordered_item_name)
        TextView tvName;
        @BindView(R.id.tv_ordered_item_price)
        TextView tvPrice;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(SessionOrderedItemModel orderedItem) {
            tvName.setText(orderedItem.formatItemDetail());
            if (!orderedItem.getItem().isVegetarian())
                tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0);
            tvPrice.setText(String.format(
                    Locale.ENGLISH, Utils.getCurrencyFormat(itemView.getContext()), orderedItem.formatCost()));
        }
    }

}
