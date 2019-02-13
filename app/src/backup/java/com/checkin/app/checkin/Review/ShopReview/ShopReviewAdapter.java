package com.checkin.app.checkin.Review.ShopReview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopReviewAdapter extends RecyclerView.Adapter<ShopReviewAdapter.ViewHolder> {
    private List<ShopReviewModel> mData;
    private ReviewInteraction mListener;

    public ShopReviewAdapter(ReviewInteraction listener) {
        mListener = listener;
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
        ImageView imUserProfilePic;
        @BindView(R.id.tv_sr_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_sr_user_stats)
        TextView tvUserStats;
        @BindView(R.id.btn_sr_review_ratings)
        Button btnReviewRating;
        @BindView(R.id.btn_sr_user_follow)
        Button btnUserFollow;
        @BindView(R.id.tv_sr_description)
        TextView tvReviewBody;
        @BindView(R.id.tv_sr_count_likes)
        TextView tvReviewLikes;
        @BindView(R.id.tv_sr_time)
        TextView tvReviewTime;
        @BindView(R.id.btn_sr_like)
        ImageView btnReviewLike;

        private ShopReviewModel mReview;
        private ImageThumbnailHolder mThumbnailHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnUserFollow.setOnClickListener(v -> mListener.onFollowClick(mReview.getUserInfo()));
            btnReviewLike.setOnClickListener(v -> {
                mListener.onToggleLike(mReview);
                btnReviewLike.setActivated(mReview.isLiked());
            });
            btnReviewRating.setOnClickListener(v -> mListener.onRatingClick(mReview));
            mThumbnailHolder = new ImageThumbnailHolder(itemView, new ImageThumbnailHolder.ImageThumbnailInteraction() {
                @Override
                public void onThumbnailClick(int index) {
                    mListener.onThumbnailClick(mReview, index);
                }
            });
        }

        public void bindData(ShopReviewModel shopReviewModel) {
            mReview = shopReviewModel;

            tvUserName.setText(shopReviewModel.getUserInfo().getDisplayName());
            String picUrl = shopReviewModel.getUserInfo().getDisplayPic();
            GlideApp.with(itemView)
                    .load(picUrl != null ? picUrl : R.drawable.cover_unknown_male)
                    .into(imUserProfilePic);
            tvUserStats.setText(shopReviewModel.formatUserStats());
            btnReviewRating.setText(shopReviewModel.formatOverallRating());
            tvReviewBody.setText(shopReviewModel.getReviewBody());
            tvReviewLikes.setText(shopReviewModel.formatCountLikes());
            tvReviewTime.setText(shopReviewModel.formatReviewTime());
            btnReviewLike.setActivated(shopReviewModel.isLiked());
            mThumbnailHolder.bindThumbnails(shopReviewModel.getThumbnails());
        }

        @OnClick({R.id.im_sr_user_pic, R.id.tv_sr_user_name})
        public void onUserClick() {
            mListener.onUserClick(mReview.getUserInfo());
        }
    }

    public interface ReviewInteraction {
        void onToggleLike(ShopReviewModel review);
        void onThumbnailClick(ShopReviewModel review, int index);
        void onRatingClick(ShopReviewModel review);
        void onFollowClick(BriefModel user);
        void onUserClick(BriefModel user);
    }
}
