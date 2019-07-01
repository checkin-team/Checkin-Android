package com.checkin.app.checkin.Menu.ActiveSessionMenu.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Misc.TextBaseAdapter;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.Utils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;

public class ActiveSessionMenuItemAdapter extends RecyclerView.Adapter<ActiveSessionMenuItemAdapter.ItemViewHolder> {
    private static final String TAG = ActiveSessionMenuItemAdapter.class.getSimpleName();

    private List<MenuItemModel> mItemsList;
    private OnItemInteractionListener mListener;
    private boolean mIsSessionActive;

    public ActiveSessionMenuItemAdapter(List<MenuItemModel> itemsList, OnItemInteractionListener listener, boolean isSessionActive) {
        mItemsList = itemsList;
        mListener = listener;
        mIsSessionActive = isSessionActive;
        notifyDataSetChanged();
    }

    public void setMenuItems(List<MenuItemModel> menuItems) {
        this.mItemsList = menuItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_as_menu_group_items;
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

    public interface OnItemInteractionListener {
        boolean onItemAdded(MenuItemModel item);

        boolean onItemLongPress(MenuItemModel item);

        boolean onItemChanged(MenuItemModel item, int count);

        int orderedItemCount(MenuItemModel item);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements DiscreteScrollView.ScrollStateChangeListener<TextBaseAdapter.TextViewHolder> {
        @BindView(R.id.tv_as_menu_item_name)
        TextView tvItemName;
        @BindView(R.id.container_as_menu_quantity_picker)
        ViewGroup containerQp;
        @BindView(R.id.container_menu_item_quantity)
        ViewGroup containerItemQuantity;
        @BindView(R.id.tv_as_menu_item_price)
        TextView tvItemPrices;
        @BindView(R.id.tv_menu_item_quantity_decrement)
        TextView tvQuantityDecrement;
        @BindView(R.id.tv_menu_item_quantity_number)
        TextView tvQuantityNumber;
        @BindView(R.id.tv_menu_item_quantity_increment)
        TextView tvQuantityIncrement;
        @BindView(R.id.btn_as_menu_item_add)
        TextView btnItemAdd;
        @BindView(R.id.tv_as_menu_item_desc)
        TextView tvDesc;
        @BindView(R.id.im_as_menu_item)
        ImageView imItem;

        private MenuItemModel mItem;
        private int mCount = 1;

        ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(v -> mItem != null && mListener.onItemLongPress(mItem));

            if (!mIsSessionActive)
                containerQp.setVisibility(View.GONE);

            tvQuantityNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (Integer.parseInt(s.toString()) < mCount && mItem.isComplexItem()) {
                        disallowDecreaseCount();
                        mCount = Math.min(mCount, Integer.parseInt(s.toString()) + 1);
                        displayQuantity(mCount);
                        return;
                    }
                    if (!mListener.onItemChanged(mItem, Integer.parseInt(s.toString()))) {
                        displayQuantity(0);
                        hideQuantitySelection();
                        return;
                    }
                    if (Integer.parseInt(s.toString()) == 0)
                        hideQuantitySelection();
                    mCount = Integer.parseInt(tvQuantityNumber.getText().toString());
                }
            });
            btnItemAdd.setOnClickListener(view -> {
                if (!mListener.onItemAdded(mItem)) {
                    Utils.toast(itemView.getContext(), "Not allowed!");
                    return;
                }
                showQuantitySelection(1);
            });

            tvQuantityDecrement.setOnClickListener(v -> {
                decreaseQuantity();
            });

            tvQuantityIncrement.setOnClickListener(v -> {
                increaseQuantity();
            });
        }

        void bindData(MenuItemModel menuItem) {
            this.mItem = menuItem;
            this.mItem.setActiveSessionItemHolder(this);

            tvItemName.setText(menuItem.getName());
            tvDesc.setText(menuItem.getDescription());
            Utils.loadImageOrDefault(imItem, menuItem.getImage(), R.drawable.shahi_paneer);
//            imItem.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//            Bitmap icon = BitmapFactory.decodeResource(imItem.getResources(), R.drawable.shahi_paneer);
//            Utils.blurImage(imItem ,icon);

            tvItemPrices.setText(Utils.formatCurrencyAmount(tvItemPrices.getContext(),menuItem.getTypeCosts().get(0)));

            int count = mListener.orderedItemCount(menuItem);
            if (count > 0)
                showQuantitySelection(count);
            else
                hideQuantitySelection();

            if (menuItem.isComplexItem()) {
                btnItemAdd.setCompoundDrawablesWithIntrinsicBounds(null,null, btnItemAdd.getContext().getResources().getDrawable(R.drawable.ic_setting),null);

                containerItemQuantity.setEnabled(false);
                containerItemQuantity.setOnClickListener(v -> {
                    disallowDecreaseCount();
                    return ;
                });
            }else{
                btnItemAdd.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                containerItemQuantity.setEnabled(true);
            }
        }

        void hideQuantitySelection() {
            containerItemQuantity.setVisibility(View.GONE);
            btnItemAdd.setVisibility(View.VISIBLE);
        }

        void showQuantitySelection(int count) {
            btnItemAdd.setVisibility(View.GONE);
            containerItemQuantity.setVisibility(View.VISIBLE);
            displayQuantity(count);
        }

        @Override
        public void onScrollStart(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {
        }

        @Override
        public void onScrollEnd(@NonNull TextBaseAdapter.TextViewHolder currentItemHolder, int adapterPosition) {
            if (adapterPosition < mCount && mItem.isComplexItem()) {
                disallowDecreaseCount();
                mCount = Math.min(mCount, adapterPosition + 1);
                displayQuantity(mCount);
                return;
            }
            if (!mListener.onItemChanged(mItem, adapterPosition)) {
                displayQuantity(0);
                hideQuantitySelection();
                return;
            }
            if (adapterPosition == 0)
                hideQuantitySelection();
            mCount = Integer.parseInt(tvQuantityNumber.getText().toString());
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TextBaseAdapter.TextViewHolder currentHolder, @Nullable TextBaseAdapter.TextViewHolder newCurrent) {
        }

        public MenuItemModel getMenuItem() {
            return mItem;
        }

        public void changeQuantity(int count) {
            mCount = count;
            try {
                displayQuantity(mCount);
            } catch (IllegalArgumentException exc) {
                displayQuantity(mCount);
            }
            if (count <= 0)
                hideQuantitySelection();
        }

        public void increaseQuantity() {
            mCount ++;
            displayQuantity(mCount);

        }public void decreaseQuantity() {
            if(mCount<=0)
                return;
            mCount --;
            displayQuantity(mCount);

        }

        private void displayQuantity(int number) {
            tvQuantityNumber.setText("" + number);
        }

        private void disallowDecreaseCount() {
            Utils.toast(itemView.getContext(), "Not allowed to change item count from here - use cart.");
        }
    }
}
