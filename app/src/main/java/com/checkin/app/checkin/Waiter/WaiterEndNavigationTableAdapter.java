package com.checkin.app.checkin.Waiter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterEndNavigationTableAdapter extends RecyclerView.Adapter<WaiterEndNavigationTableAdapter.ViewHolder> {

    private List<NavTableModel> navTableModelsList;
    private ActiveTableFragment activeTableFragment;

    public WaiterEndNavigationTableAdapter(List<NavTableModel> navTableModelsList, ActiveTableFragment activeTableFragment) {
        this.navTableModelsList = navTableModelsList;
        this.activeTableFragment = activeTableFragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_assigned_table, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_unassigned_table, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NavTableModel navTableModel = navTableModelsList.get(position);

        if (navTableModel.getHost() == null) {

        } else {
            holder.tvTableNumber.setText(navTableModel.getHost().getTableNumber());
            holder.tvName.setText(navTableModel.getHost().getCustomerName());
        }
    }


    @Override
    public int getItemViewType(final int position) {
        if (navTableModelsList.get(position).getHost() != null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return navTableModelsList.size();
    }

    public void setWaiterTableData(List<NavTableModel> navTableModels) {
        this.navTableModelsList = navTableModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTableNumber)
        TextView tvTableNumber;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvTime)
        TextView tvTime;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
