package com.checkin.app.checkin.Waiter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterEndDrawerTableAdapter extends RecyclerView.Adapter<WaiterEndDrawerTableAdapter.ViewHolder> {
    private List<RestaurantTableModel> mData;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    public void setData(List<RestaurantTableModel> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_waiter_active_table;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_waiter_table_name)
        TextView tvName;
        @BindView(R.id.tv_waiter_table_host)
        TextView tvHost;
        @BindView(R.id.tv_waiter_table_timestamp)
        TextView tvTimestamp;
        @BindView(R.id.view_waiter_table_mask)
        View viewMask;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bindData(RestaurantTableModel tableModel) {
            tvName.setText(tableModel.getTable());
            if (tableModel.getHost() != null) {
                tvHost.setText(tableModel.getHost().getDisplayName());
                viewMask.setVisibility(View.VISIBLE);
            } else {
                tvHost.setText("Standard");
            }
            tvTimestamp.setText(tableModel.getEvent().formatElapsedTime());
        }
    }
}
