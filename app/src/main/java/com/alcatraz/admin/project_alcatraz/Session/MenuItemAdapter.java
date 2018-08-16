package com.alcatraz.admin.project_alcatraz.Session;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.TextBaseAdapter;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
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

    public interface OnItemInteractionListener {
        boolean onItemAdded(View view, MenuItemModel item);
        boolean onItemLongPress(View view, MenuItemModel item);
        boolean onItemChanged(MenuItemModel item, int count);
        LongSparseArray<Integer> orderedItemsCount();
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
        mRecyclerView.setOnLongClickListener(v -> {
            int pos = mRecyclerView.getChildViewHolder(v).getAdapterPosition();
            return mItemInteractionListener.onItemLongPress(v, mItemsList.get(pos));
        });
        mRecyclerView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollListener {
        @BindView(R.id.item_title) TextView vTitle;
        @BindView(R.id.quantity_picker) DiscreteScrollView vQuantityPicker;
        @BindView(R.id.price_value) TextView vPriceValue;
        @BindView(R.id.im_item_add) ImageView imItemAdd;
        private MenuItemModel menuItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(v -> menuItem != null && mItemInteractionListener.onItemLongPress(v, menuItem));
        }

        void bindData(MenuItemModel menuItem) {
            this.menuItem = menuItem;
            vTitle.setText(menuItem.getName());
            vPriceValue.setText(Util.joinCollection(menuItem.getTypeCost(), " | "));
            if (menuItem.getCustomizationGroups() != null) {
                imItemAdd.setImageResource(R.drawable.ic_menu_item_add_customize);
            }
            vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(0, 30),
                    itemView.getResources().getColor(R.color.pinkish_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            int count = mItemInteractionListener.orderedItemsCount().get(menuItem.getId(), 0);
            if (count > 0)
                showQuantitySelection(count);
            else
                hideQuantitySelection();
            vQuantityPicker.addScrollListener(this);
            imItemAdd.setOnClickListener(view -> {
                if (!mItemInteractionListener.onItemAdded(view, menuItem)) {
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
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
            if (Math.abs(scrollPosition) < 1f)
                return;
            if (newPosition == 0) {
                hideQuantitySelection();
            }
            mItemInteractionListener.onItemChanged(menuItem, newPosition);
        }
    }
}
