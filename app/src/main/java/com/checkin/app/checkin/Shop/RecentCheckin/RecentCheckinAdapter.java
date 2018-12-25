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
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecentCheckinAdapter extends RecyclerView.Adapter<RecentCheckinAdapter.ViewHolder> {
    private List<UserCheckinModel> userCheckins;
    private RecentCheckinInteraction mListener;

    RecentCheckinAdapter(List<UserCheckinModel> userCheckinModel, RecentCheckinInteraction listener) {
        this.userCheckins = userCheckinModel;
        this.mListener = listener;
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
            return R.layout.item_shop_recent_checkin_left;
        else
            return R.layout.item_shop_recent_checkin_right;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_rc_user_pic)
        ImageView imProfilePic;
        @BindView(R.id.im_checkin_status)
        ImageView imStatus;
        @BindView(R.id.tv_rc_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_rc_checkin_time)
        TextView tvCheckinTime;

        private UserCheckinModel userCheckinModel;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(UserCheckinModel usermodel) {
            this.userCheckinModel = usermodel;

            tvCheckinTime.setText(usermodel.formatCheckinTime());
            tvUserName.setText(usermodel.getUserInfo().getDisplayName());
            String picurl = usermodel.getUserInfo().getDisplayPic();
            if (picurl == null) {
                GlideApp.with(itemView)
                        .load((usermodel.getGender() == UserModel.GENDER.MALE)
                                ? R.drawable.cover_unknown_male : R.drawable.cover_unknown_female)
                        .into(imProfilePic);
            } else {
                GlideApp.with(itemView).load(picurl).into(imProfilePic);
            }
            imStatus.setActivated(usermodel.isActive());
        }

        @OnClick(R.id.incl_checkin_card)
        void onClickCard() {
            mListener.onClickUserCheckin(this.userCheckinModel);
        }
    }

    public interface RecentCheckinInteraction {
        void onClickUserCheckin(UserCheckinModel userCheckinModel);
    }
}
