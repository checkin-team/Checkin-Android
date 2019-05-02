package com.checkin.app.checkin.Waiter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.DebouncedOnClickListener;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.checkin.app.checkin.session.model.TableSessionModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterEndDrawerTableAdapter extends RecyclerView.Adapter<WaiterEndDrawerTableAdapter.ViewHolder> {
    private List<RestaurantTableModel> mData;
    @Nullable
    private WaiterWorkActivity mListener;

    WaiterEndDrawerTableAdapter(@Nullable WaiterWorkActivity listener) {
        this.mListener = listener;
    }

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

    interface OnTableClickListener {
        void onTableClick(RestaurantTableModel restaurantTableModel);
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
        @BindView(R.id.container_waiter_table_name)
        CardView cvWaiterTableName;
        @BindView(R.id.container_waiter_table)
        CardView cvWaiterTable;

        private RestaurantTableModel mRestaurantTable;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            cvWaiterTable.setOnClickListener(new DebouncedOnClickListener(2000) {
                @Override
                public void onDebouncedClick(View v) {
                    if (mListener != null && mRestaurantTable != null)
                        mListener.onTableClick(mRestaurantTable);
                }
            });
        }

        public void bindData(RestaurantTableModel tableModel) {
            this.mRestaurantTable = tableModel;

            TableSessionModel tableSessionModel = tableModel.getTableSession();
            if (tableSessionModel != null) {
                tvName.setText(tableModel.getTable());
                if (tableSessionModel.getHost() != null) {
                    tvHost.setText(tableSessionModel.getHost().getDisplayName());
                    viewMask.setVisibility(View.VISIBLE);
                } else {
                    tvHost.setText("Standard");
                }
                if (tableSessionModel.getEvent() != null)
                    tvTimestamp.setText(tableSessionModel.getEvent().formatElapsedTime());
                cvWaiterTableName.setVisibility(View.VISIBLE);
                tvTimestamp.setVisibility(View.VISIBLE);
            } else {
                tvHost.setText(tableModel.getTable());
                cvWaiterTableName.setVisibility(View.GONE);
                tvTimestamp.setVisibility(View.INVISIBLE);
            }
        }
    }
}
