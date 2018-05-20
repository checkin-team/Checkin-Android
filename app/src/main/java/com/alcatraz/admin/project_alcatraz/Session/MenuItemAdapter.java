package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.StartSnapHelper;

import java.util.ArrayList;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder> {
    private ArrayList<MenuItem> mItemsList;
    private MenuGroupAdapter.GroupViewHolder mGroupViewHolder;
    private MenuGroupAdapter menuGroupAdapter;
    private RecyclerView mRecyclerView;

    public MenuItemAdapter(ArrayList<MenuItem> itemsList, MenuGroupAdapter groupAdapter, MenuGroupAdapter.GroupViewHolder groupViewHolder) {
        mItemsList = itemsList;
        mGroupViewHolder = groupViewHolder;
        menuGroupAdapter = groupAdapter;
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
        ItemViewHolder(View itemView) {
            super(itemView);
            vTitle = itemView.findViewById(R.id.item_title);
        }

        public void bindData(MenuItem menuItem) {
            vTitle.setText(menuItem.title);
        }
    }
}
