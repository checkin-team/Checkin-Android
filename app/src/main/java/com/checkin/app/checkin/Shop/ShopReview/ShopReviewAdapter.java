package com.checkin.app.checkin.Shop.ShopReview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopReviewAdapter extends RecyclerView.Adapter<ShopReviewAdapter.ViewHolder> {
    private List<ShopReviewModel> mData;

    public ShopReviewAdapter() {
    }

    public ShopReviewAdapter(List<ShopReviewModel> data) {
        this.mData = data;
    }

    public void updateShopReview(List<ShopReviewModel> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_shop_review;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_sr_user_pic)
        ImageView imProfilePic;
        @BindView(R.id.tv_sr_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_sr_user_stats)
        TextView tvUserStats;
        @BindView(R.id.btn_sr_review_ratings)
        Button btnCountRating;
        @BindView(R.id.btn_sr_user_follow)
        Button btnFollow;
        @BindView(R.id.tv_sr_description)
        TextView tvDescriptiveReview;
        @BindView(R.id.tv_sr_count_likes)
        TextView tvNoOfLikes;
        @BindView(R.id.tv_sr_time)
        TextView tvLastEditedTime;
        @BindView(R.id.im_sr_like)
        ImageView imLikeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ShopReviewModel shopReviewModel) {
            tvUserName.setText(shopReviewModel.getUserInfo().getDisplayName());
            String picurl = shopReviewModel.getUserInfo().getDisplayPic();
            GlideApp.with(itemView).load(picurl != null ? picurl : R.drawable.cover_unknown_male).into(imProfilePic);
            tvUserStats.setText(shopReviewModel.formatUserStats());
            btnCountRating.setText(String.format("RATED  %s", shopReviewModel.getRatingCount()));
            tvDescriptiveReview.setText(shopReviewModel.getdescriptionBody());
            tvNoOfLikes.setText(shopReviewModel.formatCountLikes());
            tvLastEditedTime.setText(shopReviewModel.formatReviewTime());
            imLikeBtn.setActivated(shopReviewModel.isLiked());
        }
    }
}
