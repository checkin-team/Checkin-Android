package com.checkin.app.checkin.Menu.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.QuantityPickerView;
import com.checkin.app.checkin.Utility.SwipeRevealLayout;
import com.checkin.app.checkin.Utility.Utils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
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

    public interface MenuCartInteraction {
        void onOrderedItemRemark(OrderedItemModel item);

        void onOrderedItemChanged(OrderedItemModel item, int count);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollStateChangeListener {
        @BindView(R.id.tv_menu_cart_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_menu_cart_item_price)
        TextView tvItemPrice;
        @BindView(R.id.tv_menu_cart_item_customized)
        TextView tvItemCustomized;
        @BindView(R.id.im_menu_cart_item_type)
        ImageView imType;
        @BindView(R.id.qp_menu_cart_item_quantity)
        QuantityPickerView qpItemQuantity;

        private OrderedItemModel mItem;
        private int scrollPos;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

//            btnItemEdit.setOnClickListener(v -> mListener.onOrderedItemRemark(mItem));
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

            tvItemName.setText(item.getItemModel().getName());
            tvItemPrice.setText(Utils.formatCurrencyAmount(tvItemPrice.getContext(), item.getCost()));
            if(item.isCustomized())
                tvItemCustomized.setVisibility(View.VISIBLE);
            else
                tvItemCustomized.setVisibility(View.GONE);


            if(item.getItemModel().isVegetarian())
                imType.setImageDrawable(imType.getContext().getResources().getDrawable(R.drawable.ic_veg));
            else
                imType.setImageDrawable(imType.getContext().getResources().getDrawable(R.drawable.ic_non_veg));

            qpItemQuantity.scrollToPosition(item.getQuantity());
            scrollPos = item.getQuantity();
        }

        public void setItemType(boolean isVegetarian){

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
}
