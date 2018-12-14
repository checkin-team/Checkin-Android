package com.checkin.app.checkin.Shop.RecentCheckin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.UserCheckinModel;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentCheckinAdapter extends RecyclerView.Adapter<RecentCheckinAdapter.ViewHolder> {
    private List<UserCheckinModel> userCheckins;

    public RecentCheckinAdapter(List<UserCheckinModel> userCheckinModel) {
        this.userCheckins = userCheckinModel;
    }

    public void updateUserCheckins(List<UserCheckinModel> userCheckinModels) {
        this.userCheckins = userCheckinModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(userCheckins.get(position));
    }

    @Override
    public int getItemCount() {
        return userCheckins != null ? userCheckins.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return R.layout.item_left_recent;
        else
            return R.layout.item_right_recent;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_display_pic)
        ImageView imProfilePic;
//        @BindView(R.id.ring)
        ImageView imring;
//        @BindView(R.id.recent_name)
        TextView tvUserName;
//        @BindView(R.id.recent_time)
        TextView recent_time;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(UserCheckinModel usermodel) {
            recent_time.setText(usermodel.formatCheckinTime());
            tvUserName.setText(usermodel.getUserInfo().getDisplayName());
            String picurl = usermodel.getUserInfo().getDisplayPic();
            GlideApp.with(itemView).load(picurl != null ? picurl : R.drawable.cover_unknown_male).into(imProfilePic);
            imring.setActivated(usermodel.isActive());
        }
    }
}
