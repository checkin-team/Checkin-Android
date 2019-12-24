package com.checkin.app.checkin.session.activesession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.models.SessionCustomerModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionMemberAdapter extends HeaderFooterRecyclerViewAdapter {

    private List<SessionCustomerModel> mUsers;
    private SessionMemberInteraction mListener;

    public ActiveSessionMemberAdapter(List<SessionCustomerModel> users, SessionMemberInteraction mListener) {
        this.mUsers = users;
        this.mListener = mListener;
    }

    @Override
    public boolean useHeader() {
        return false;
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
        if (mUsers != null) {
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

    public interface SessionMemberInteraction {
        void onUnacceptedMemberClicked(SessionCustomerModel customerModel);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_as_member_user)
        CircleImageView imUser;
        @BindView(R.id.tv_as_member_user)
        TextView tvUser;
        @BindView(R.id.tv_as_member_unaccepted)
        TextView tvPendingRequest;

        private SessionCustomerModel mCustomerModel;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(v -> {
                if (!mCustomerModel.isAccepted())
                    mListener.onUnacceptedMemberClicked(mCustomerModel);
            });
        }

        void bindData(SessionCustomerModel customer) {
            this.mCustomerModel = customer;
            tvUser.setText(customer.getUser().getDisplayName());
            Utils.loadImageOrDefault(imUser, customer.getUser().getDisplayPic(), R.drawable.cover_unknown_male);
            if (!customer.isAccepted()){
                tvPendingRequest.setVisibility(View.VISIBLE);
                imUser.setVisibility(View.GONE);
            } else{
                tvPendingRequest.setVisibility(View.GONE);
                imUser.setVisibility(View.VISIBLE);
            }
        }
    }
}
