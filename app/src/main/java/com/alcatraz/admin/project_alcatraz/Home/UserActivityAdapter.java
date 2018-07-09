package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.ViewHolder> {
    private List<User> mUsers;
    private Context mContext;

    UserActivityAdapter(List<User> users, Context context) {
        mUsers = users;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
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

    public void setUsers(List<User> users) {
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

        void bindData(User user) {
            vUserName.setText(user.getUsername());
            Glide.with(vUserImage.getContext()).load(R.drawable.dummy_alex)
                    .into(vUserImage);
        }
    }
}
