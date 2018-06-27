package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.HeaderFooterRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderedItemAdapter extends HeaderFooterRecyclerViewAdapter {
    private List<OrderedItem> mOrderedItems;
    private Context mContext;

    OrderedItemAdapter(Context context) {
        mContext = context;
        mOrderedItems = new ArrayList<>();
    }

    @Override
    public boolean useHeader() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public boolean useFooter() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_ordered_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindEmptyData();
        if (getBasicItemCount() > 0)
            ((ViewHolder) holder).bindFooterView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(position);
    }

    @Override
    public int getBasicItemCount() {
        return mOrderedItems.size();
    }

    @Override
    public int getBasicItemType(int position) {
        return R.layout.session_ordered_item;
    }

    public void addItems(OrderedItem[] items) {
        mOrderedItems.addAll(Arrays.asList(items));
        notifyItemRangeInserted(getItemCount() - items.length, items.length);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ordered_item_no) TextView mItemNo;
        @BindView(R.id.ordered_item_info) TextView mItemInfo;
        @BindView(R.id.ordered_item_price) TextView mItemPrice;
        @BindView(R.id.ordered_item_status) CheckedTextView mItemStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(int position) {
            OrderedItem item = mOrderedItems.get(position);
            mItemNo.setText(String.valueOf(position + 1));
            mItemInfo.setText(String.format(Locale.ENGLISH, "%s (%s) x %d", item.getTitle(), item.getType(), item.getCount()));
            mItemPrice.setText(String.valueOf(item.getPrice()));
            mItemStatus.setText(item.getRemainingSeconds() == 0 ? "DONE" : "WAIT");
        }

        public void bindFooterView() {
            float totalPrice = 0f;
            for (OrderedItem orderedItem: mOrderedItems) {
                totalPrice += orderedItem.getPrice();
            }
            mItemInfo.setText("TOTAL");
            mItemPrice.setText(String.valueOf(totalPrice));
        }

        public void bindEmptyData() {
            mItemNo.setText("");
            mItemInfo.setText("");
            mItemStatus.setText("");
            mItemStatus.setChecked(false);
            mItemStatus.setCheckMarkDrawable(null);
            mItemPrice.setText("");
        }
    }
}
