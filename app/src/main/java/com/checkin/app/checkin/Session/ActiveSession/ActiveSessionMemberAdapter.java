package com.checkin.app.checkin.Session.ActiveSession;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionMemberAdapter extends HeaderFooterRecyclerViewAdapter {

    private List<ActiveSessionMemberModel> mUsers;
    private SessionMemberInteraction memberInterface;

    ActiveSessionMemberAdapter(List<ActiveSessionMemberModel> users, SessionMemberInteraction memberInterface){
        this.mUsers = users;
        this.memberInterface = memberInterface;
    }

    @Override
    public boolean useHeader() {
        return true;
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session_add_member, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public boolean useFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        if (mUsers != null){
            ((ItemViewHolder) holder).bindData(mUsers.get(position));
        }
    }

    @Override
    public int getBasicItemCount() {
        return (mUsers != null) ? mUsers.size() : 0;
    }

    @Override
    public int getBasicItemType(int position) {
        return R.layout.item_session_member;
    }

    public void setUsers(List<ActiveSessionMemberModel> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_user)
        CircleImageView imUser;
        @BindView(R.id.tv_user) TextView tvUser;
        ItemViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(UserModel user) {
            tvUser.setText(user.getUsername());
            Glide.with(tvUser.getContext()).load(user.getProfilePic()).into(imUser);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View view) {
            super(view);
            view.findViewById(R.id.im_add_member).setOnClickListener((v) -> memberInterface.onAddMemberClicked());
        }
    }

    interface SessionMemberInteraction {
        void onAddMemberClicked();
    }
}
