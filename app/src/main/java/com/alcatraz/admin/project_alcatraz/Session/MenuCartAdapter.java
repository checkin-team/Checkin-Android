package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.SwipeRevealLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuCartAdapter extends RecyclerView.Adapter<MenuCartAdapter.ViewHolder> {
    private List<OrderedItemModel> orderedItems;
    private RecyclerView mRecyclerView;
    private OnCartInteractionListener mCartInteractionListener;

    public interface OnCartInteractionListener {
        void onItemRemoved(OrderedItemModel item);
        void onItemEdited(OrderedItemModel item);
    }

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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_item_edit)
        ImageButton btnItemEdit;
        @BindView(R.id.btn_item_remove) ImageButton btnItemRemove;
        @BindView(R.id.item_name) TextView tvItemName;
        @BindView(R.id.item_price) TextView tvItemPrice;
        @BindView(R.id.cart_item_reveal_layout) SwipeRevealLayout swipeRevealLayout;
        @BindView(R.id.card_item)
        CardView cardItem;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(final OrderedItemModel item) {
            tvItemName.setText(item.getItem().getName() + " x" + item.getCount());
            tvItemPrice.setText(String.valueOf(item.getPrice()));
            cardItem.setOnClickListener(v -> {
                if (swipeRevealLayout.isOpened())
                    swipeRevealLayout.close(true);
                else
                    swipeRevealLayout.open(true);
            });
            btnItemRemove.setOnClickListener(v -> mCartInteractionListener.onItemRemoved(item));
        }
    }
}
