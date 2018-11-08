package com.checkin.app.checkin.User.NonPersonalProfile;

// Created by viBHU on 17-10-2018

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCheckinsAdapter extends RecyclerView.Adapter<CustomCheckinsAdapter.MyViewHolder> {

    private List<CheckinsPOJO> mcheckins;
    public CustomCheckinsAdapter(List<CheckinsPOJO> checkins){
        this.mcheckins = checkins;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_checkins_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CheckinsPOJO checkins = mcheckins.get(position);
        holder.mResturantsName.setText(checkins.getmResturantName());
        holder.mUserCheckins.setText(checkins.getmUserCheckins());
    }

    @Override
    public int getItemCount() {
        Log.e("TAG",mcheckins.toString());
        return mcheckins.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_shop_name)TextView mResturantsName;
        @BindView(R.id.tv_user_visits)TextView mUserCheckins;
        @BindView(R.id.im_shop_pic)CircleImageView mResturantsPic;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
