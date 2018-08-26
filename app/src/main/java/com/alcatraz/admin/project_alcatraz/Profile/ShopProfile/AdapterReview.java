package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.MoreText;
import com.borjabravo.readmoretextview.ReadMoreTextView;

import java.util.ArrayList;
import java.util.List;

    public class AdapterReview extends RecyclerView.Adapter<AdapterReview.MyView> {

        private List<ReviewsItem> list;
        private ArrayList<Boolean> bool=new ArrayList<>();

        public static class MyView extends RecyclerView.ViewHolder {

            public TextView name, followers, time,rating;
            public de.hdodenhof.circleimageview.CircleImageView profile;
            TextView review;

            public MyView(View view, int x) {
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
        }


        public AdapterReview(List<ReviewsItem> list) {
            this.list = list;
            for (int i = 0; i <list.size() ; i++) {
                bool.add(Boolean.FALSE);

            }

        }

        @Override
        public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType!=0) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_review, parent, false);
                return new MyView(itemView,1);
            }
            return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_shop_review, parent, false),0);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyView holder, final int position) {
            if(position==0)
                return;
            ReviewsItem review = list.get(position);
            holder.time.setText(review.time);
               // holder.review.readMore=true;
            holder.review.setText(review.review);
            holder.profile.setImageResource(review.img);
            holder.followers.setText(review.followers);
            holder.rating.setText("RATED   "+review.rating);
            holder.name.setText(review.name);

            /*holder.review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.review.readMore=!holder.review.readMore;
                    holder.review.setT
                }
            });*/


        }
        public static void update(TextView v)
        {

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position==0?0:1;
        }

        public List<ReviewsItem> getList(){
            return list;
        }
    }
