package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.TextBaseAdapter;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.angmarch.views.NiceSpinnerArrowOnly;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private ArrayList<MenuItem> mItemsList;
    private MenuGroupAdapter.GroupViewHolder mGroupViewHolder;
    private RecyclerView mRecyclerView;
    private static OnItemClickListener btnPriceClickListener;

    MenuItemAdapter(ArrayList<MenuItem> itemsList, MenuGroupAdapter.GroupViewHolder groupViewHolder) {
        mItemsList = itemsList;
        mGroupViewHolder = groupViewHolder;
    }

    public interface OnItemClickListener {
        void onItemOrdered(View view, OrderedItem item);
    }

    public void setOnPriceClickListener(OnItemClickListener listener) {
        btnPriceClickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("ItemsList", "Touched");
                view.getParent().requestDisallowInterceptTouchEvent(true);
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
        return mItemsList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView vTitle;
        NiceSpinnerArrowOnly vSpinner;
        DiscreteScrollView vQuantityPicker;
        View vPriceLayout;
        ImageView vPriceButton;
        TextView vPriceValue;

        ItemViewHolder(View itemView) {
            super(itemView);
            vTitle = itemView.findViewById(R.id.item_title);
            vPriceLayout = itemView.findViewById(R.id.price_layout);
            vPriceButton = itemView.findViewById(R.id.price_button);
            vPriceValue = itemView.findViewById(R.id.price_value);
            vQuantityPicker = itemView.findViewById(R.id.quantity_picker);
            vSpinner = itemView.findViewById(R.id.spinner);
        }

        void bindData(MenuItem menuItem) {
            vTitle.setText(menuItem.title);
            vSpinner.attachDataSource(Arrays.asList(menuItem.types));
            vPriceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = vQuantityPicker.getCurrentItem();
                    int typeIndex = vSpinner.getSelectedIndex();
                    btnPriceClickListener.onItemOrdered(view, OrderedItem.order(menuItem, quantity, typeIndex));
                }
            });
            vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(0, 30),
                    itemView.getResources().getColor(R.color.light_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            vQuantityPicker.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                @Override
                public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                    if (adapterPosition > 0) {
                        vPriceButton.setImageResource(R.drawable.menu_selected_item_price);
                        vPriceLayout.setClickable(true);
                        vPriceValue.setText(String.valueOf(menuItem.costs[vSpinner.getSelectedIndex()] * adapterPosition));
                    }
                    else {
                        vPriceButton.setImageResource(R.drawable.menu_item_price);
                        vPriceLayout.setClickable(false);
                        vPriceValue.setText(R.string.menu_item_default_price);
                    }
                }
            });
            vSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int quantity = vQuantityPicker.getCurrentItem();
                    if (quantity > 0) {
                        vPriceValue.setText(String.valueOf(menuItem.costs[vSpinner.getSelectedIndex()] * quantity));
                    }
                }
            });
        }
    }
}
