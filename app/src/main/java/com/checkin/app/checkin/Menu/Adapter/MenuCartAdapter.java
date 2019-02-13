package com.checkin.app.checkin.Menu.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.QuantityPickerView;
import com.checkin.app.checkin.Utility.SwipeRevealLayout;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuCartAdapter extends RecyclerView.Adapter<MenuCartAdapter.ViewHolder> {
    private List<OrderedItemModel> orderedItems;
    private MenuCartInteraction mListener;

    public MenuCartAdapter(MenuCartInteraction cartInteractionListener) {
        orderedItems = new ArrayList<>();
        mListener = cartInteractionListener;
    }

    @Override
    public int getItemCount() {
        return orderedItems != null ? orderedItems.size() : 0;
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
        return R.layout.item_menu_cart;
    }

    public void setOrderedItems(List<OrderedItemModel> orderedItems) {
        this.orderedItems = orderedItems;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollStateChangeListener {
        @BindView(R.id.btn_menu_cart_item_edit) ImageButton btnItemEdit;
        @BindView(R.id.btn_menu_cart_item_remove) ImageButton btnItemRemove;
        @BindView(R.id.tv_menu_cart_item_name) TextView tvItemName;
        @BindView(R.id.tv_menu_cart_item_price) TextView tvItemPrice;
        @BindView(R.id.tv_menu_cart_item_extra) TextView tvItemExtra;
        @BindView(R.id.qp_menu_cart_item_quantity) QuantityPickerView qpItemQuantity;
        @BindView(R.id.sr_menu_cart_item) SwipeRevealLayout srCartItem;
        @BindView(R.id.container_menu_cart_item) ViewGroup containerMenuCartItem;

        private OrderedItemModel mItem;
        private int scrollPos;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            containerMenuCartItem.setOnClickListener(v -> {
                if (srCartItem.isOpened())
                    srCartItem.close(true);
                else
                    srCartItem.open(true);
            });

            btnItemRemove.setOnClickListener(v -> mListener.onOrderedItemRemoved(mItem));
            btnItemEdit.setOnClickListener(v -> mListener.onOrderedItemRemark(mItem));
            qpItemQuantity.setSlideOnFling(true);
            qpItemQuantity.addScrollStateChangeListener(this);

            ItemClickSupport.addTo(qpItemQuantity).setOnItemClickListener((rv, position, v) -> qpItemQuantity.smoothScrollToPosition(position));
        }

        void bindData(final OrderedItemModel item) {
            try {
                mItem = item.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            tvItemName.setText(String.format(Locale.ENGLISH, "%s", item.getItemModel().getName()));
            tvItemPrice.setText(String.format(Locale.ENGLISH, "\u20B9 %.2f", item.getCost()));
            tvItemExtra.setText(String.format(
                    Locale.ENGLISH, "%d %s %s", item.getQuantity(), (item.getTypeName() != null ? item.getTypeName() : ""), (item.isCustomized() ? "(Customized)" : "")));
            qpItemQuantity.scrollToPosition(item.getQuantity());
            scrollPos = item.getQuantity();
        }

        @Override
        public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
            if (scrollPos != adapterPosition) {
                scrollPos = adapterPosition;
                mListener.onOrderedItemChanged(mItem, scrollPos);
            }
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
        }
    }

    public interface MenuCartInteraction {
        void onOrderedItemRemark(OrderedItemModel item);
        void onOrderedItemRemoved(OrderedItemModel item);
        void onOrderedItemChanged(OrderedItemModel item, int count);
    }
}
