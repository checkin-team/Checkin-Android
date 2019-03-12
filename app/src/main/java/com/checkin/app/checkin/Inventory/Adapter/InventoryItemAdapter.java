package com.checkin.app.checkin.Inventory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.checkin.app.checkin.Inventory.InventoryMenuItemInteraction;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Misc.TextBaseAdapter;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.QuantityPickerView;
import com.checkin.app.checkin.Utility.QuantityPickerView.Direction;
import com.checkin.app.checkin.Utility.Utils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ItemViewHolder> {
    private static final String TAG = InventoryItemAdapter.class.getSimpleName();

    private List<InventoryItemModel> mItemsList;
    private OnItemInteractionListener mListener;
    private boolean mIsSessionActive;
    InventoryMenuItemInteraction mInventoryMenuItemInteraction;

    public InventoryItemAdapter(List<InventoryItemModel> itemsList, OnItemInteractionListener listener, boolean isSessionActive, InventoryMenuItemInteraction inventoryMenuItemInteraction) {
        mItemsList = itemsList;
        mListener = listener;
        mIsSessionActive = isSessionActive;
        mInventoryMenuItemInteraction = inventoryMenuItemInteraction;
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollStateChangeListener<TextBaseAdapter.TextViewHolder> {
        @BindView(R.id.tv_menu_item_name) TextView tvItemName;
        @BindView(R.id.container_menu_quantity_picker) ViewGroup containerQp;
        @BindView(R.id.qp_menu_item_quantity) QuantityPickerView qpItemQuantity;
        @BindView(R.id.tv_menu_item_price) TextView tvItemPrices;
        @BindView(R.id.btn_menu_item_add) Button btnItemAdd;
        @BindView(R.id.switch_menu_item_availability)
        Switch switchItemAvailability ;

        private InventoryItemModel mItem;
        private int scrollPos = 1;

        ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            containerQp.setVisibility(View.GONE);
            switchItemAvailability.setVisibility(View.VISIBLE);

            itemView.setOnLongClickListener(v -> mItem != null && mListener.onItemLongPress(mItem));

            switchItemAvailability.setOnClickListener(v -> {
                mInventoryMenuItemInteraction.onClickItemAvailability(mItem,switchItemAvailability.isChecked());
            });

            if (!mIsSessionActive)
                containerQp.setVisibility(View.GONE);

            ItemClickSupport.addTo(qpItemQuantity).setOnItemClickListener((recyclerView, position, v) -> {
                if (position < qpItemQuantity.getCurrentItem() && mItem.isComplexItem())
                    disallowDecreaseCount();
                else
                    qpItemQuantity.smoothScrollToPosition(position);
            });
            qpItemQuantity.addScrollStateChangeListener(this);
            btnItemAdd.setOnClickListener(view -> {
                if (!mListener.onItemAdded(mItem)) {
                    Utils.toast(itemView.getContext(), "Not allowed!");
                    return;
                }
                showQuantitySelection(1);
            });
        }

        void bindData(InventoryItemModel menuItem) {
            this.mItem = menuItem;
            this.mItem.setItemHolder(this);

            tvItemName.setText(menuItem.getName());
            tvItemPrices.setText(String.format(
                    Locale.ENGLISH, Utils.getCurrencyFormat(itemView.getContext()),
                    Utils.joinCollection(menuItem.getTypeCosts(), " | ")));

            switchItemAvailability.setChecked(menuItem.isAvailable());

            int count = mListener.orderedItemCount(menuItem);
            if (count > 0)
                showQuantitySelection(count);
            else
                hideQuantitySelection();

            if (menuItem.isComplexItem()) {
                btnItemAdd.setBackgroundResource(R.drawable.btn_menu_item_add_customize);

                qpItemQuantity.setDisabledDirection(Direction.START);
                qpItemQuantity.setCallable(() -> {
                    disallowDecreaseCount();
                    return null;
                });
            }
            qpItemQuantity.setSlideOnFling(!menuItem.isComplexItem());
        }

        void hideQuantitySelection() {
            qpItemQuantity.setVisibility(View.GONE);
            btnItemAdd.setVisibility(View.VISIBLE);
        }

        void showQuantitySelection(int count) {
            btnItemAdd.setVisibility(View.GONE);
            qpItemQuantity.setVisibility(View.VISIBLE);
            qpItemQuantity.scrollToPosition(count);
        }

        @Override
        public void onScrollStart(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {
        }

        @Override
        public void onScrollEnd(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {
            if (adapterPosition < scrollPos && mItem.isComplexItem()) {
                disallowDecreaseCount();
                scrollPos = Math.min(scrollPos, adapterPosition + 1);
                qpItemQuantity.smoothScrollToPosition(scrollPos);
                return;
            }
            if (!mListener.onItemChanged(mItem, adapterPosition)) {
                qpItemQuantity.scrollToPosition(0);
                hideQuantitySelection();
                return;
            }
            if (adapterPosition == 0)
                hideQuantitySelection();
            scrollPos = qpItemQuantity.getCurrentItem();
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TextBaseAdapter.TextViewHolder currentHolder, @Nullable TextBaseAdapter.TextViewHolder newCurrent) {
        }

        public InventoryItemModel getMenuItem() {
            return mItem;
        }

        public void changeQuantity(int count) {
            scrollPos = count;
            try {
                qpItemQuantity.smoothScrollToPosition(count);
            } catch (IllegalArgumentException exc) {
                qpItemQuantity.scrollToPosition(count);
            }
            if (count <= 0)
                hideQuantitySelection();
        }

        private void disallowDecreaseCount() {
            Utils.toast(itemView.getContext(), "Not allowed to change item count from here - use cart.");
        }
    }

    public interface OnItemInteractionListener {
        boolean onItemAdded(InventoryItemModel item);
        boolean onItemLongPress(InventoryItemModel item);
        boolean onItemChanged(InventoryItemModel item, int count);
        int orderedItemCount(InventoryItemModel item);
    }
}
