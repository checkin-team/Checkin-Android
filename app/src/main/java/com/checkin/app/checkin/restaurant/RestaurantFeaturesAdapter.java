package com.checkin.app.checkin.restaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class RestaurantFeaturesAdapter extends RecyclerView.Adapter<RestaurantFeaturesAdapter.ViewHolder> {
    String[] extradata;


    public RestaurantFeaturesAdapter(String[]extradata){
        this.extradata=extradata;
    }
    public void updateData(String[] extradata){
        this.extradata=extradata;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RestaurantFeaturesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_info,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        holder.bindData(extradata[position]);
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantFeaturesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return extradata.length;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;

        }
        public void bindData(String str){
          TextView tv= itemView.findViewById(R.id.restaurant_info);
          tv.setText(str);
        }
    }
}
