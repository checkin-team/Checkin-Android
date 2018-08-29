package com.checkin.app.checkin.Menu;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.SnapToNViews;
import com.checkin.app.checkin.Utility.TextBaseAdapter;
import com.checkin.app.checkin.Utility.Util;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private final String TAG = MenuItemAdapter.class.getSimpleName();

    private List<MenuItemModel> mItemsList;
    private RecyclerView mRecyclerView;
    private static OnItemInteractionListener mItemInteractionListener;

    MenuItemAdapter(List<MenuItemModel> itemsList) {
        mItemsList = itemsList;
    }

    public void setItemInteractionListener (OnItemInteractionListener listener) {
        mItemInteractionListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_item_card_layout;
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollListener<TextBaseAdapter.TextViewHolder> {
        @BindView(R.id.item_title) TextView vTitle;
        @BindView(R.id.quantity_picker) DiscreteScrollView vQuantityPicker;
        @BindView(R.id.price_value) TextView vPriceValue;
        @BindView(R.id.im_item_add) ImageView imItemAdd;
        private MenuItemModel menuItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(v -> menuItem != null && mItemInteractionListener.onItemLongPress(menuItem));
        }

        void bindData(MenuItemModel menuItem) {
            this.menuItem = menuItem;
            vTitle.setText(menuItem.getName());
            vPriceValue.setText(Util.joinCollection(menuItem.getTypeCost(), " | "));
            if (!menuItem.getCustomizationGroups().isEmpty()) {
                imItemAdd.setImageResource(R.drawable.ic_menu_item_add_customize);
            }
            int count = mItemInteractionListener.orderedItemCount(menuItem);
            if (count > 0)
                showQuantitySelection(count);
            else
                hideQuantitySelection();
//            if (menuItem.isComplexItem())
                vQuantityPicker.setSlideOnFling(true);
            vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(0, 30),
                    itemView.getResources().getColor(R.color.pinkish_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            vQuantityPicker.addScrollListener(this);
            imItemAdd.setOnClickListener(view -> {
                if (!mItemInteractionListener.onItemAdded(this)) {
                    Toast.makeText(vTitle.getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showQuantitySelection(1);
            });
        }

        void hideQuantitySelection() {
            vQuantityPicker.setVisibility(View.GONE);
            imItemAdd.setVisibility(View.VISIBLE);
        }

        void showQuantitySelection(int count) {
            imItemAdd.setVisibility(View.GONE);
            vQuantityPicker.setVisibility(View.VISIBLE);
            vQuantityPicker.scrollToPosition(count);
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TextBaseAdapter.TextViewHolder currentHolder, @Nullable TextBaseAdapter.TextViewHolder newCurrent) {
            if (Math.abs(scrollPosition) < 1f)
                return;
            if (newPosition == 0) {
                hideQuantitySelection();
            }
            boolean increased = newPosition - currentPosition > 0;
            if (menuItem.isComplexItem()) {
                if (Math.abs(newPosition - currentPosition) > 1) {
                    Log.e(TAG, "Overscroll: " + (newPosition - currentPosition));
                    newPosition = currentPosition + (increased ? 1 : -1);
                    vQuantityPicker.smoothScrollToPosition(newPosition);
                    return;
                }
            }
            if (!mItemInteractionListener.onItemChanged(this, newPosition, increased)) {
                Log.e(TAG, "Curr: " + currentPosition + ", new: " + newPosition);
                vQuantityPicker.scrollToPosition(currentPosition);
            }
        }

        public MenuItemModel getMenuItem() {
            return menuItem;
        }
    }

    public interface OnItemInteractionListener {
        boolean onItemAdded(ItemViewHolder holder);
        boolean onItemLongPress(MenuItemModel item);
        boolean onItemChanged(ItemViewHolder holder, int count, boolean increased);
        int orderedItemCount(MenuItemModel item);
    }
}
