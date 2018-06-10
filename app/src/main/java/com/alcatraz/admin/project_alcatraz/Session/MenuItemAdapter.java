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
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.angmarch.views.NiceSpinnerArrowOnly;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private ArrayList<MenuItem> mItemsList;
    private MenuGroupAdapter.GroupViewHolder mGroupViewHolder;
    private RecyclerView mRecyclerView;
    private static OnItemClickListener mOnItemClickListener;

    public MenuItemAdapter(ArrayList<MenuItem> itemsList, MenuGroupAdapter.GroupViewHolder groupViewHolder) {
        mItemsList = itemsList;
        mGroupViewHolder = groupViewHolder;
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, MenuItem menuItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
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

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView vTitle;
        View vPriceLayout;
        ImageView vPriceButton;
        MenuItem mMenuItem;
        NiceSpinnerArrowOnly vSpinner;
        DiscreteScrollView vQuantityPicker;

        ItemViewHolder(View itemView) {
            super(itemView);
            vTitle = itemView.findViewById(R.id.item_title);
            vPriceLayout = itemView.findViewById(R.id.price_layout);
            vPriceButton = itemView.findViewById(R.id.price_button);
            vPriceLayout.setOnClickListener(this);

            vQuantityPicker = itemView.findViewById(R.id.quantity_picker);
            vQuantityPicker.setAdapter(new TextBaseAdapter(
                    Util.range(0, 30),
                    itemView.getResources().getColor(R.color.light_grey),
                    itemView.getResources().getColor(R.color.brownish_grey))
            );
            vQuantityPicker.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                @Override
                public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                    if (adapterPosition != 0) {
                        vPriceButton.setImageResource(R.drawable.menu_selected_item_price);
                    }
                    else {
                        vPriceButton.setImageResource(R.drawable.menu_item_price);
                    }
                }
            });

            vSpinner = itemView.findViewById(R.id.spinner);
            vSpinner.attachDataSource(Arrays.asList(itemView.getResources().getStringArray(R.array.spinner_options)));
            vSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e("Spinner", "Selected: #" + vSpinner.getSelectedIndex());
                }
            });
        }

        void bindData(MenuItem menuItem) {
            mMenuItem = menuItem;
            vTitle.setText(menuItem.title);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClicked(view, mMenuItem);
        }
    }
}
