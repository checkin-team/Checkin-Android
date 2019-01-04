package com.checkin.app.checkin.Session.ActiveSession;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/*public class ActiveSessionRecommendations extends RecyclerView.Adapter<ActiveSessionRecommendations.ViewHolder> {
    List<RestaurantModel> mShops;

    ActiveSessionRecommendations(List<RestaurantModel> shops) {
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
        return R.layout.item_session_recommendations;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image)
        ImageView item_image;
        @BindView(R.id.tv_item_name)
        TextView tv_item_name;
        @BindView(R.id.tv_price) TextView tv_price;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}*/
