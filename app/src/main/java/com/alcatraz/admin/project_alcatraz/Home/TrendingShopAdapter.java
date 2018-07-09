package com.alcatraz.admin.project_alcatraz.Home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Shop.Shop;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pchmn.materialchips.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TrendingShopAdapter extends RecyclerView.Adapter<TrendingShopAdapter.ViewHolder> {
    List<Shop> mShops;

    TrendingShopAdapter(List<Shop> shops) {
        mShops = shops;
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
        return mShops.size();
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

        void bindData(Shop shop) {
            vShopName.setText(shop.getName());
            vShopCategory.setText(shop.getCategory());
            Glide.with(vShopName.getContext()).load(R.drawable.dummy_shop)
                    .into(vShopImage);
        }
    }
}
