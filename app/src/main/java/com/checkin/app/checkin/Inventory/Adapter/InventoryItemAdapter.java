package com.checkin.app.checkin.Inventory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ItemViewHolder> {
    private List<InventoryItemModel> mItemsList;
    private boolean mIsSessionActive;
    private OnItemInteractionListener mListener;

    public InventoryItemAdapter(List<InventoryItemModel> itemsList, OnItemInteractionListener listener, boolean isSessionActive) {
        mItemsList = itemsList;
        mListener = listener;
        mIsSessionActive = isSessionActive;
    }

    public void setMenuItems(List<InventoryItemModel> menuItems) {
        this.mItemsList = menuItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_menu_item;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(mItemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList != null ? mItemsList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_menu_item_name)
        TextView tvItemName;
        @BindView(R.id.container_menu_quantity_picker)
        ViewGroup containerQp;
        @BindView(R.id.tv_menu_item_price)
        TextView tvItemPrices;
        @BindView(R.id.switch_menu_item_availability)
        Switch switchItemAvailability;

        private InventoryItemModel mItem;

        ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            containerQp.setVisibility(View.GONE);
            switchItemAvailability.setVisibility(View.VISIBLE);

            itemView.setOnLongClickListener(v -> mItem != null && mListener.onMenuItemShowInfo(mItem));

            switchItemAvailability.setOnClickListener(view -> mListener.onClickItemAvailability(mItem, switchItemAvailability.isChecked()));

            if (!mIsSessionActive)
                containerQp.setVisibility(View.GONE);
        }

        void bindData(InventoryItemModel menuItem) {
            this.mItem = menuItem;
            this.mItem.setItemHolder(this);

            tvItemName.setText(menuItem.getName());
            tvItemPrices.setText(String.format(
                    Locale.ENGLISH, Utils.getCurrencyFormat(itemView.getContext()),
                    Utils.joinCollection(menuItem.getTypeCosts(), " | ")));

            switchItemAvailability.setChecked(menuItem.isAvailable());
        }
    }

    public interface OnItemInteractionListener {
        boolean onMenuItemShowInfo(InventoryItemModel item);

        void onClickItemAvailability(InventoryItemModel mItem, boolean isChecked);
    }
}
