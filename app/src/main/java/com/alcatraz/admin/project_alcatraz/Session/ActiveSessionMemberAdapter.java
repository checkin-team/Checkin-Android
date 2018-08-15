package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.alcatraz.admin.project_alcatraz.Utility.HeaderFooterRecyclerViewAdapter;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionMemberAdapter extends HeaderFooterRecyclerViewAdapter {

    private List<User> mUsers;
    private Context mContext;

    ActiveSessionMemberAdapter(Context context, List<User> users){
        this.mContext = context;
        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public boolean useHeader() {
        return true;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean useFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        if (mUsers != null){
            ((ViewHolder) holder).bindData(mUsers.get(position));
        }
    }

    @Override
    public int getBasicItemCount() {
        if (mUsers != null) return mUsers.size();
        else return 0;
    }

    @Override
    public int getBasicItemType(int position) {
        return R.layout.session_user_layout;
    }

    public void setUsers(List<User> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_user)
        CircleImageView imUser;
        @BindView(R.id.tv_user) TextView tvUser;
        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(User user) {
            tvUser.setText(user.getUsername());
            Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(imUser);
        }
    }

}
