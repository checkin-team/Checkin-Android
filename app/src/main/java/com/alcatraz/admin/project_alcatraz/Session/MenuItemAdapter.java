package com.alcatraz.admin.project_alcatraz.Session;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.ItemClickSupport;
import com.alcatraz.admin.project_alcatraz.Utility.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private final String TAG = MenuItemAdapter.class.getSimpleName();

    private List<MenuItem> mItemsList;
    private RecyclerView mRecyclerView;
    private static OnItemInteractionListener mItemInteractionListener;

    MenuItemAdapter(List<MenuItem> itemsList) {
        mItemsList = itemsList;
    }

    public interface OnItemInteractionListener {
        boolean onItemAdded(View view, MenuItem item);
        boolean onItemLongPress(View view, MenuItem item);
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
        mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = mRecyclerView.getChildViewHolder(v).getAdapterPosition();
                return mItemInteractionListener.onItemLongPress(v, mItemsList.get(pos));
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title) TextView vTitle;
//        @BindView(R.id.quantity_picker) DiscreteScrollView vQuantityPicker;
        @BindView(R.id.price_value) TextView vPriceValue;
//        @BindView(R.id.item_type_slider) RangeBar vSliderBar;
        @BindView(R.id.im_item_add) ImageView imItemAdd;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(MenuItem menuItem) {
            vTitle.setText(menuItem.getName());
            /*boolean showSlider = menuItem.getCosts().size() > 1;
            if (showSlider) {
                vPriceValue.setVisibility(View.GONE);
                vSliderBar.setVisibility(View.VISIBLE);
                vSliderBar.setTickStart(0);
                vSliderBar.setTickEnd(menuItem.getCosts().size() - 1);
                vSliderBar.setFormatter(new IRangeBarFormatter() {
                    @Override
                    public String format(String value) {
                        return String.valueOf(menuItem.getCosts().get(Integer.valueOf(value)));
                    }
                });
                vSliderBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                    @Override
                    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                        Log.e(TAG, "left value: " + leftPinValue + ", right: " + rightPinValue);
                        Log.e(TAG, "left index: " + leftPinIndex + ", right: " + rightPinIndex);
                    }
                });
            } else {
                vPriceValue.setText(String.valueOf(menuItem.getCosts().get(0)));
            }*/
            vPriceValue.setText(Util.joinCollection(menuItem.getCosts(), " | "));
            imItemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean res = mItemInteractionListener.onItemAdded(view, menuItem);
                    if (!res) {
                        Toast.makeText(vTitle.getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(1, 30),
                    itemView.getResources().getColor(R.color.pinkish_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            vQuantityPicker.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                @Override
                public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                }
            });*/
        }
    }
}
