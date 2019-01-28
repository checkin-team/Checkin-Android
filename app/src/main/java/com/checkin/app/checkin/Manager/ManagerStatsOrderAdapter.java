package com.checkin.app.checkin.Manager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerStatsOrderAdapter extends RecyclerView.Adapter<ManagerStatsOrderAdapter.ShopManagerTableStaticsHolder> {

    private List<RestaurantStaticsModel.TrendingOrder> trendingOrder;
    
    public ManagerStatsOrderAdapter(){
    }

    @NonNull
    @Override
    public ShopManagerTableStaticsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_manager_stats_order, parent, false);
        return new ShopManagerTableStaticsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopManagerTableStaticsHolder holder, int position) {
        RestaurantStaticsModel.TrendingOrder data = trendingOrder.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return trendingOrder != null ? trendingOrder.size() : 0;
    }

    void setRestaurantStaticsList(List<RestaurantStaticsModel.TrendingOrder> trendingOrder) {
        this.trendingOrder = trendingOrder;
        notifyDataSetChanged();
    }

    class ShopManagerTableStaticsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_shop_manager_table_statics_item)
        ImageView ivShopManagerTableStaticsItem;
        @BindView(R.id.tv_shop_manager_table_statics_item)
        TextView tvShopManagerTableStaticsItem;
        @BindView(R.id.tv_shop_manager_table_statics_item_description)
        TextView tvShopManagerTableStaticsItemDescription;

        ShopManagerTableStaticsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(RestaurantStaticsModel.TrendingOrder data) {
            RestaurantItemModel item = data.getItem();
            String revenueGenerated = data.getRevenueGenerated();
            if (item != null){
                boolean isVegetarian = item.getIsVegetarian();
                String name = item.getName();

                if (isVegetarian)
                    ivShopManagerTableStaticsItem.setImageResource(R.drawable.ic_veg);
                else
                    ivShopManagerTableStaticsItem.setImageResource(R.drawable.ic_non_veg);

                tvShopManagerTableStaticsItem.setText(name);
                tvShopManagerTableStaticsItemDescription.setText(revenueGenerated);
            }
        }
    }
}
