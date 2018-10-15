package com.checkin.app.checkin.Menu;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.QuantityPickerView;
import com.checkin.app.checkin.Utility.QuantityPickerView.Direction;
import com.checkin.app.checkin.Utility.TextBaseAdapter;
import com.checkin.app.checkin.Utility.Util;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private final String TAG = MenuItemAdapter.class.getSimpleName();

    private List<MenuItemModel> mItemsList;
    private RecyclerView mRecyclerView;
    private static OnItemInteractionListener mItemInteractionListener;
    private boolean activate=true;

    MenuItemAdapter(List<MenuItemModel> itemsList) {
        mItemsList = itemsList;
    }

    public void setItemInteractionListener (OnItemInteractionListener listener) {
        mItemInteractionListener= listener;
    }

    public void setActivate(boolean activate){
        this.activate=activate;
    }
    public boolean getActivate(){
        return activate;
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

        if(!getActivate())
        {  view.findViewById(R.id.quantity_picker_layout).setVisibility(View.GONE);}
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollStateChangeListener<TextBaseAdapter.TextViewHolder> {
        @BindView(R.id.item_title) TextView vTitle;
        @BindView(R.id.quantity_picker) QuantityPickerView vQuantityPicker;
        @BindView(R.id.price_value) TextView vPriceValue;
        @BindView(R.id.im_item_add) ImageView imItemAdd;
        private MenuItemModel menuItem;
        private int scrollPos = 1;

        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnLongClickListener(v -> menuItem != null && mItemInteractionListener.onItemLongPress(menuItem));
        }

        @SuppressLint("ClickableViewAccessibility")
        void bindData(MenuItemModel menuItem) {
            this.menuItem = menuItem;
            this.menuItem.setItemHolder(this);

            vTitle.setText(menuItem.getName());
            vPriceValue.setText(Util.joinCollection(menuItem.getTypeCost(), " | "));
            if (!menuItem.getCustomizationGroups().isEmpty()) {
                imItemAdd.setImageResource(R.drawable.ic_menu_item_add_customize);
            }
            int count=0;
            if(mItemInteractionListener!=null)
            count = mItemInteractionListener.orderedItemCount(menuItem);
            if (count > 0)
                showQuantitySelection(count);
            else
                hideQuantitySelection();
            if (menuItem.isComplexItem()) {
                vQuantityPicker.setDisabledDirection(Direction.START);
                vQuantityPicker.setCallable(() -> {
                    disallowDecreaseCount();
                    return null;
                });
            }
            vQuantityPicker.setSlideOnFling(!menuItem.isComplexItem());
            ItemClickSupport.addTo(vQuantityPicker).setOnItemClickListener((recyclerView, position, v) -> {
                if (position < vQuantityPicker.getCurrentItem() && menuItem.isComplexItem())
                    disallowDecreaseCount();
                else
                    vQuantityPicker.smoothScrollToPosition(position);
            });
            vQuantityPicker.addScrollStateChangeListener(this);
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
        public void onScrollStart(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScrollEnd(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {
            if (adapterPosition < scrollPos && menuItem.isComplexItem()) {
                disallowDecreaseCount();
                scrollPos = Math.min(scrollPos, adapterPosition + 1);
                vQuantityPicker.smoothScrollToPosition(scrollPos);
                return;
            }
            int quantity = adapterPosition;
            if (!mItemInteractionListener.onItemChanged(this, quantity)) {
                vQuantityPicker.scrollToPosition(0);
                hideQuantitySelection();
                return;
            }
            if (quantity == 0)
                hideQuantitySelection();
            scrollPos = vQuantityPicker.getCurrentItem();
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TextBaseAdapter.TextViewHolder currentHolder, @Nullable TextBaseAdapter.TextViewHolder newCurrent) {
        }

        public MenuItemModel getMenuItem() {
            return menuItem;
        }

        public void changeQuantity(int count) {
            scrollPos = count;
            try {
                vQuantityPicker.smoothScrollToPosition(count);
            } catch (IllegalArgumentException exc) {
                vQuantityPicker.scrollToPosition(count);
            }
            if (count <= 0)
                hideQuantitySelection();
        }
    }

    private void disallowDecreaseCount() {
        FluentSnackbar.create(mRecyclerView)
            .create("Cannot decrease count of a complex item from here, use cart.")
            .warningBackgroundColor()
            .textColorRes(R.color.brownish_grey)
            .duration(Snackbar.LENGTH_SHORT)
            .show();
    }

    public interface OnItemInteractionListener {
        boolean onItemAdded(ItemViewHolder holder);
        boolean onItemLongPress(MenuItemModel item);
        boolean onItemChanged(ItemViewHolder holder, int count);
        int orderedItemCount(MenuItemModel item);
    }
}
