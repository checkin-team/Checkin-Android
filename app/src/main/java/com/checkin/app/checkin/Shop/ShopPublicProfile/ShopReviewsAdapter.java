package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopReviewPOJO;

import java.util.ArrayList;
import java.util.List;

import static com.checkin.app.checkin.Utility.TextLineWrapper.makeTextViewResizable;

/**
 * Created by Jogi Miglani on 07-10-2018.
 */

public class ShopReviewsAdapter extends RecyclerView.Adapter<ShopReviewsAdapter.ShopReviewViewHolder> {

    private List<ShopReviewPOJO> list_members = new ArrayList<>();
    ShopReviewViewHolder holder;

    public ShopReviewsAdapter() {}

    @Override
    public ShopReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_recycler_view,parent,false);
        ShopReviewViewHolder viewHolder=new ShopReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShopReviewViewHolder holder, int position) {
        ShopReviewPOJO list_items=list_members.get(position);
        holder.name.setText(list_items.getName());
        holder.reviewAndFollowers.setText(list_items.getReviewsAndFollowers());
        holder.totalVisits.setText(list_items.getTotalVisits());
        holder.tv.setText(list_items.getFullReview());
        makeTextViewResizable(holder.tv, 3, true);
        holder.time.setText(list_items.getTime());
        holder.rating.setText(Integer.toString(list_items.getRating()));

    }

    public void setListContent(List<ShopReviewPOJO> list_members){
        this.list_members=list_members;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    public static class ShopReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,reviewAndFollowers,time,totalVisits,tv,rating;
        public ShopReviewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=(TextView)itemView.findViewById(R.id.tv_name);
            reviewAndFollowers=(TextView)itemView.findViewById(R.id.tv_reviews_followers);
            totalVisits=(TextView)itemView.findViewById(R.id.tv_visits);
            tv=(TextView)itemView.findViewById(R.id.tv_full_review);
            time=(TextView)itemView.findViewById(R.id.tv_review_time);
            rating=(TextView)itemView.findViewById(R.id.rating_number);
        }
        @Override
        public void onClick(View v) {

        }
    }
    public void removeAt(int position) {
        list_members.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, list_members.size());
    }





}

