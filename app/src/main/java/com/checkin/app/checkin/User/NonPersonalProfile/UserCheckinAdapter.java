package com.checkin.app.checkin.User.NonPersonalProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Util;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCheckinAdapter extends RecyclerView.Adapter<UserCheckinAdapter.UserCheckinHolder> {

    private List<UserCheckinModel> mList;

    @NonNull
    @Override
    public UserCheckinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_user_resturants_list, parent, false);
        return new UserCheckinHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCheckinHolder holder, int position) {
        UserCheckinModel data = mList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    void addUserCheckinData(List<UserCheckinModel> mList) {
        this.mList = mList;
    }

    class UserCheckinHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_checkin_image)
        ImageView ivUserCheckinImage;
        @BindView(R.id.tv_user_checkin_name)
        TextView tvUserCheckinName;
        @BindView(R.id.tv_user_checkin_address)
        TextView tvUserCheckinAddress;
        @BindView(R.id.tv_user_checkin_total_visits)
        TextView tvUserCheckinTotalVisits;

        UserCheckinHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(UserCheckinModel data) {
            String mLocation = data.getLocation();
            int mCount = data.getCountVisits();
            String mName = data.getShop().getDisplayName();
            String mPic = data.getShop().getDisplayPic();

            Util.loadImageOrDefault(ivUserCheckinImage,mPic,R.drawable.cover_restaurant_unknown);
            tvUserCheckinName.setText(mName);
            tvUserCheckinAddress.setText(mLocation);
            tvUserCheckinTotalVisits.setText(String.format(Locale.getDefault(),"Total Visits : %d", mCount));
        }
    }
}
