package com.checkin.app.checkin.Shop.ShopReview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopReviewAdapter extends RecyclerView.Adapter<ShopReviewAdapter.ViewHolder> {
    private List<ShopReviewModel> shopReviewModel;

    public ShopReviewAdapter() {
    }

    public ShopReviewAdapter(List<ShopReviewModel> shopReviewModel) {
        this.shopReviewModel = shopReviewModel;
    }

    public void updateShopReview(List<ShopReviewModel> shopReview) {
        this.shopReviewModel = shopReview;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_review,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(shopReviewModel.get(position));

    }
    @Override
    public int getItemCount() {
        return shopReviewModel != null ? shopReviewModel.size():0;
    }
   /* public int getItemViewType(int position)
    {
        return R.layout.item_user_review;
    }*/

        public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_ur_profile)
        ImageView imProfilePic;
        @BindView(R.id.tv_ur_name)
        TextView tvUserName;
        @BindView(R.id.tv_ur_review_nd_follower)
        TextView tvCountReviewFollowers;
        @BindView(R.id.tv_ur_rating)
        TextView tvCountRating;
        @BindView(R.id.tv_ur_descriptive_review)
        TextView tvDescriptiveReview;
        @BindView(R.id.tv_ur_NoOfLikes)
        TextView tvNoOfLikes;
        @BindView(R.id.tv_ur_time)
        TextView tvLastActiveTime;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(ShopReviewModel shopReviewModel) {
            tvUserName.setText(shopReviewModel.getUserInfo().getDisplayName());
            String picurl = shopReviewModel.getUserInfo().getDisplayPic();
            GlideApp.with(itemView).load(picurl != null ? picurl : R.drawable.cover_unknown_male).into(imProfilePic);
            int reviews = shopReviewModel.getNoOfReviews();
            int followers = shopReviewModel.getNoOfFollowers();
            tvCountReviewFollowers.setText(Integer.toString(reviews)+" reviews"+","+" "+Integer.toString(followers)+" followers");
            tvCountRating.setText(Integer.toString(shopReviewModel.getRatingCount()));
            tvDescriptiveReview.setText(shopReviewModel.getdescriptionBody());
            tvNoOfLikes.setText(Integer.toString(shopReviewModel.getNoOfLikes()));
            tvLastActiveTime.setText(Integer.toString(shopReviewModel.getTime())+" HOURS AGO");
        }
    }
}
