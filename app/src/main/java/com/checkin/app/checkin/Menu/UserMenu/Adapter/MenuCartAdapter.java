package com.checkin.app.checkin.Menu.UserMenu.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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
        holder.etSpecialInstructions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
//                        mListener.onOrderedItemRemark(mItem, etSpecialInstructions.getText().toString());
                    orderedItems.get(position).setRemarks(holder.etSpecialInstructions.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_as_menu_cart;
    }

    public void setOrderedItems(List<OrderedItemModel> orderedItems) {
        this.orderedItems = orderedItems;
//        notifyDataSetChanged();
    }

    public interface MenuCartInteraction {
        void onOrderedItemRemark(OrderedItemModel item, String s);

        void onOrderedItemChanged(OrderedItemModel item, int count);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_menu_cart_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_menu_cart_item_price)
        TextView tvItemPrice;
        @BindView(R.id.tv_menu_cart_item_customized)
        TextView tvItemCustomized;
        @BindView(R.id.im_menu_cart_item_type)
        ImageView imType;
        @BindView(R.id.tv_menu_item_quantity_decrement)
        TextView tvQuantityDecrement;
        @BindView(R.id.tv_menu_item_quantity_number)
        TextView tvQuantityNumber;
        @BindView(R.id.tv_menu_item_quantity_increment)
        TextView tvQuantityIncrement;
        @BindView(R.id.et_as_menu_cart_special_instruction)
        EditText etSpecialInstructions;

        private OrderedItemModel mItem;
        private int mCount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvQuantityDecrement.setOnClickListener(v -> {
                decreaseQuantity();
            });

            tvQuantityIncrement.setOnClickListener(v -> {
                increaseQuantity();
            });

            tvQuantityNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                        mCount = Integer.parseInt(s.toString());
                    mListener.onOrderedItemChanged(mItem, Integer.parseInt(s.toString()));

                }
            });
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

            displayQuantity(item.getQuantity());
            mCount = item.getQuantity();
        }

        public void setItemType(boolean isVegetarian){

        }

        /*private void setQuantityChangeListener(){

        }


        private void setOnSpecialInstructionsListener(){

        }*/

        public void increaseQuantity() {
            mCount ++;
            displayQuantity(mCount);

        }
        public void decreaseQuantity() {
            if(mCount<=0)
                return;
            mCount --;
            displayQuantity(mCount);

        }

        private void displayQuantity(int number) {
            tvQuantityNumber.setText("" + number);
        }
    }
}
