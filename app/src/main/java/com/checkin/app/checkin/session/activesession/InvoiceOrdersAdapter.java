package com.checkin.app.checkin.session.activesession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceOrdersAdapter extends RecyclerView.Adapter<InvoiceOrdersAdapter.ViewHolder> {

    private List<SessionOrderedItemModel> mOrderedItems;
    private OrderedItemClick mClickListener;

    public InvoiceOrdersAdapter(List<SessionOrderedItemModel> orderedItems, OrderedItemClick clickListener) {
        this.mOrderedItems = orderedItems;
        this.mClickListener = clickListener;
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
        @BindView(R.id.im_ms_ordered_item_cancel)
        ImageView imCancelOrder;
        private SessionOrderedItemModel morderedItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(v -> {
                mClickListener.cancelOrderedItem(morderedItem);
            });
        }

        void bindData(SessionOrderedItemModel orderedItem) {
            morderedItem = orderedItem;
            if(mClickListener!=null)
                imCancelOrder.setVisibility(View.VISIBLE);
            tvName.setText(orderedItem.formatItemDetail());
            if (!orderedItem.getItem().isVegetarian())
                tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0);
            tvPrice.setText(String.format(
                    Locale.ENGLISH, Utils.getCurrencyFormat(itemView.getContext()), orderedItem.formatCost()));
        }
    }

    public interface OrderedItemClick{
        void cancelOrderedItem(SessionOrderedItemModel orderedItem);
    }

}
