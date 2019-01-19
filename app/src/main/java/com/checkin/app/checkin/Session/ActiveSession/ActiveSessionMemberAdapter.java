package com.checkin.app.checkin.Session.ActiveSession;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionMemberAdapter extends HeaderFooterRecyclerViewAdapter {

    private List<SessionCustomerModel> mUsers;
    private SessionMemberInteraction memberInterface;

    ActiveSessionMemberAdapter(List<SessionCustomerModel> users, SessionMemberInteraction memberInterface){
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
        return R.layout.item_active_session_member;
    }

    public void setUsers(List<SessionCustomerModel> users) {
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

        void bindData(SessionCustomerModel customer) {
            tvUser.setText(customer.getUser().getDisplayName());
            Utils.loadImageOrDefault(imUser,customer.getUser().getDisplayPic(),R.drawable.cover_unknown_male);
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
