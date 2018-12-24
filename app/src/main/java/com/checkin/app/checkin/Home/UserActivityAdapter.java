package com.checkin.app.checkin.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.ViewHolder> {
    private List<UserModel> mUsers;
    private Context mContext;

    UserActivityAdapter(List<UserModel> users, Context context) {
        mUsers = users;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    public UserModel getUserByPosition(int position) {
        return mUsers.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.home_user_activities_item;
    }

    public void setUsers(List<UserModel> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image) ImageView vUserImage;
        @BindView(R.id.user_name) TextView vUserName;

            ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(UserModel user) {
            vUserName.setText(user.getUsername());
            Glide.with(vUserImage.getContext()).load(user.getProfilePic())
                    .into(vUserImage);
        }
    }
}
