package com.checkin.app.checkin.Shop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyView> {

    private List<ShopReview> shopReviews;

    public ReviewsAdapter(List<ShopReview> shopReviews) {
        this.shopReviews = shopReviews;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType!=0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_review, parent, false);
            return new MyView(itemView,1);
        }
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_shop_review, parent, false),0);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        if(position==0)
            return;
        holder.bindData(shopReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return shopReviews.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?0:1;
    }

    public List<ShopReview> getShopReviews(){
        return shopReviews;
    }

    public static class MyView extends RecyclerView.ViewHolder {

        public TextView name, followers, time,rating;
        public de.hdodenhof.circleimageview.CircleImageView profile;
        TextView review;

        MyView(View view, int x) {
            super(view);
            if(x==0)
                return;
            name = (TextView) view.findViewById(R.id.review_profile_name);
            followers = view.findViewById(R.id.reviews_follwers);
            rating = view.findViewById(R.id.rating);
            time=view.findViewById(R.id.time);
            profile=view.findViewById(R.id.photo_review);
            review=view.findViewById(R.id.the_review);
        }

        void bindData(ShopReview review) {
            time.setText(review.getTime().toString());
            this.review.setText(review.getReview());
            this.followers.setText("12");
            this.rating.setText("RATED " + review.getRating());
            this.name.setText(review.getUserName());
            GlideApp.with(itemView.getContext()).load(review.getUserProfilePic()).into(profile);
        }
    }
}
