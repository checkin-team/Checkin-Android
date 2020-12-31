package com.checkin.app.checkin.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.manager.models.ManagerStatsModel;
import com.checkin.app.checkin.menu.models.MenuItemBriefModel;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerStatsOrderAdapter extends RecyclerView.Adapter<ManagerStatsOrderAdapter.ShopManagerTableStaticsHolder> {

    private List<ManagerStatsModel.ItemRevenue> mData;

    public ManagerStatsOrderAdapter() {
    }

    public void setData(List<ManagerStatsModel.ItemRevenue> itemRevenue) {
        this.mData = itemRevenue;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopManagerTableStaticsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_analysis_item_revenue, parent, false);
        return new ShopManagerTableStaticsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopManagerTableStaticsHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    static class ShopManagerTableStaticsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_manager_stats_trending_order_veg)
        ImageView imItemStatus;
        @BindView(R.id.tv_manager_stats_trending_order_name)
        TextView tvItemName;
        @BindView(R.id.tv_manager_stats_trending_order_revenue)
        TextView tvRevenue;

        ShopManagerTableStaticsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ManagerStatsModel.ItemRevenue data) {
            MenuItemBriefModel item = data.getItem();
            double revenueGenerated = data.getRevenueContribution();
            if (item != null) {
                if (item.getIsVegetarian())
                    imItemStatus.setImageResource(R.drawable.ic_veg);
                else
                    imItemStatus.setImageResource(R.drawable.ic_non_veg);

                tvItemName.setText(item.getName());
                tvRevenue.setText(String.format(Locale.getDefault(), "%.2f %% revenue contribution", revenueGenerated));
            }
        }
    }
}
