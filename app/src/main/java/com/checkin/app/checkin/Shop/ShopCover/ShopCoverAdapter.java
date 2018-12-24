package com.checkin.app.checkin.Shop.ShopCover;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.ArrayList;

public class ShopCoverAdapter extends RecyclerView.Adapter<ShopCoverAdapter.ViewHolder>{

    private ArrayList<ShopModel> cover;

    public ShopCoverAdapter(ArrayList<ShopModel> cover) {
        this.cover = cover;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_cover_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.BindData(cover.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView SrNo;
        public ViewHolder(View itemView) {
            super(itemView);
            this.image =itemView.findViewById(R.id.im_shop_cover);
            this.SrNo = itemView.findViewById(R.id.tv_shop_cover_SrNo);
        }

        void BindData(ShopModel model){
            int im = model.getImage();
            int txt = model.getSrNo();
            Glide.with(itemView).load(im!=0?im : R.drawable.dummy_shop).into(image);
            SrNo.setText(txt);

        }
    }
}
