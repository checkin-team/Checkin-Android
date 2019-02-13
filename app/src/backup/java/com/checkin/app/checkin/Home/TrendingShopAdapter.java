package com.checkin.app.checkin.Home;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrendingShopAdapter extends RecyclerView.Adapter<TrendingShopAdapter.ViewHolder> {
    List<RestaurantModel> mShops;

    TrendingShopAdapter(List<RestaurantModel> shops) {
        mShops = shops;
    }

    public void setData(List<RestaurantModel> data) {
        this.mShops = data;
        notifyDataSetChanged();
    }

    public RestaurantModel getByPosition(int pos) {
        return mShops.get(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mShops.get(position));
    }

    @Override
    public int getItemCount() {
        return mShops != null ? mShops.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.home_trending_shops_item;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_image) ImageView vShopImage;
        @BindView(R.id.shop_name) TextView vShopName;
        @BindView(R.id.shop_category) TextView vShopCategory;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(RestaurantModel shop) {
            vShopName.setText(shop.getName());
            Glide.with(vShopName.getContext()).load(shop.getLogoUrl())
                    .into(vShopImage);
        }
    }
}
