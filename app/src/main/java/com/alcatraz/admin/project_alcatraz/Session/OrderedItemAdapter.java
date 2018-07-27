package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderedItemAdapter extends RecyclerView.Adapter<OrderedItemAdapter.ViewHolder> {
    private List<OrderedItem> mOrderedItems;

    OrderedItemAdapter(Context context) {
        mOrderedItems = new ArrayList<>();
    }

    public void addItems(OrderedItem[] items) {
        mOrderedItems.addAll(Arrays.asList(items));
        notifyItemRangeInserted(getItemCount() - items.length, items.length);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    public OrderedItem getItemWithId(int itemId) {
        for (OrderedItem item: mOrderedItems) {
            if (Util.equalsObjectField(item, "id", itemId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mOrderedItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.menu_cart_item_layout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name) TextView tvItemName;
        @BindView(R.id.item_price) TextView tvItemPrice;
        @BindView(R.id.btn_item_edit) TextView btnItemEdit;
        @BindView(R.id.btn_item_remove) TextView btnItemRemove;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            OrderedItem item = mOrderedItems.get(position);
            tvItemName.setText(item.getItem().getName());
            tvItemPrice.setText(String.valueOf(item.getPrice()));
            btnItemEdit.setOnClickListener(view -> {
            });
            btnItemRemove.setOnClickListener(view -> {
            });
        }
    }
}
