package com.checkin.app.checkin.RestaurantActivity.Waiter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterEndNavigationTableAdapter extends RecyclerView.Adapter<WaiterEndNavigationTableAdapter.ViewHolder> {
    List<TableModel> mTables;


    WaiterEndNavigationTableAdapter(List<TableModel> tables) {
        mTables = tables;

    }

    @NonNull
    @Override
    public WaiterEndNavigationTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new WaiterEndNavigationTableAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull WaiterEndNavigationTableAdapter.ViewHolder holder, int position) {
        holder.bindData(mTables.get(position));
    }


    @Override
    public int getItemViewType(final int position) {
        return R.layout.table_status_recy;
    }

    @Override
    public int getItemCount() {
            return mTables.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.table_label)
        TextView tableLabel;
        @BindView(R.id.table_customer)
        TextView tableCustomer;


        ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }

        void bindData(TableModel table) {
            tableLabel.setText(table.getName());
            if(table.getUserStandard().contains("PREMIUM")) {
                tableCustomer.setTextColor(Color.parseColor("#af1014"));
            }
            tableCustomer.setText(table.getUserStandard());

        }

    }
}
