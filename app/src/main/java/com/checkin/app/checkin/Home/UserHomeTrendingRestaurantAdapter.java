package com.checkin.app.checkin.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Home.Model.TrendingRestaurantsModel;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeTrendingRestaurantAdapter extends RecyclerView.Adapter<UserHomeTrendingRestaurantAdapter.ViewHolder> {
    private List<TrendingRestaurantsModel> mTrendingRest;

    public UserHomeTrendingRestaurantAdapter() {

    }

    public void setData(List<TrendingRestaurantsModel> data) {
        this.mTrendingRest = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mTrendingRest.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrendingRest != null ? mTrendingRest.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_home_trending_restaurants;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_as_trending_dish)
        ImageView imDish;
        private TrendingRestaurantsModel mItemModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bindData(TrendingRestaurantsModel itemModel) {
            this.mItemModel = itemModel;
        }
    }
}
