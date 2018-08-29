package com.checkin.app.checkin.Menu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.SwipeRevealLayout;
import com.checkin.app.checkin.Utility.TextBaseAdapter;
import com.checkin.app.checkin.Utility.Util;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuCartAdapter extends RecyclerView.Adapter<MenuCartAdapter.ViewHolder> {
    private List<OrderedItemModel> orderedItems;
    private RecyclerView mRecyclerView;
    private OnCartInteractionListener mCartInteractionListener;

    MenuCartAdapter(OnCartInteractionListener cartInteractionListener) {
        orderedItems = new ArrayList<>();
        mCartInteractionListener = cartInteractionListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return orderedItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(orderedItems.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_cart_item_layout;
    }

    public List<OrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<OrderedItemModel> orderedItems) {
        this.orderedItems = orderedItems;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollListener {
        @BindView(R.id.btn_item_edit)
        ImageButton btnItemEdit;
        @BindView(R.id.btn_item_remove) ImageButton btnItemRemove;
        @BindView(R.id.item_name) TextView tvItemName;
        @BindView(R.id.item_price) TextView tvItemPrice;
        @BindView(R.id.item_extra) TextView tvItemExtra;
        @BindView(R.id.quantity_picker) DiscreteScrollView vQuantityPicker;
        @BindView(R.id.cart_item_reveal_layout) SwipeRevealLayout swipeRevealLayout;
        @BindView(R.id.card_item)
        CardView cardItem;
        private OrderedItemModel mItem;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(final OrderedItemModel item) {
            mItem = item;
            tvItemName.setText(item.getItem().getName() + " x" + item.getCount());
            setPrice(item.getCost());
            cardItem.setOnClickListener(v -> {
                if (swipeRevealLayout.isOpened())
                    swipeRevealLayout.close(true);
                else
                    swipeRevealLayout.open(true);
            });
            tvItemExtra.setText(
                    (item.getTypeName() != null ? item.getTypeName() : "") +
                    (item.isCustomized() ? " (Customized)" : "")
            );
            btnItemRemove.setOnClickListener(v -> mCartInteractionListener.onItemRemoved(item));
            btnItemEdit.setOnClickListener(v -> mCartInteractionListener.onItemRemark(item));
            if (item.getItem().isComplexItem())
                vQuantityPicker.setSlideOnFling(true);
            vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(0, 30),
                    itemView.getResources().getColor(R.color.pinkish_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            vQuantityPicker.addScrollListener(this);
            vQuantityPicker.scrollToPosition(item.getCount());
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
            if (Math.abs(scrollPosition) < 1f)
                return;
            mCartInteractionListener.onItemChanged(mItem, newPosition);
//            setPrice(mItem.getCost());
        }

        private void setPrice(double cost) {
            tvItemPrice.setText(String.format(Locale.ENGLISH, "\u20B9 %.2f", cost));
        }
    }

    public interface OnCartInteractionListener {
        void onItemRemark(OrderedItemModel item);
        void onItemRemoved(OrderedItemModel item);
        void onItemChanged(OrderedItemModel item, int count);
    }
}
