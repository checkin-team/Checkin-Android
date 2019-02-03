package com.checkin.app.checkin.User.Friendship;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.Friendship.FriendshipModel.FRIEND_STATUS;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendshipAdapter extends RecyclerView.Adapter<FriendshipAdapter.MyViewHolder>  {
    private List<FriendshipModel> mFriends;
    private FriendshipInteraction mInteractionListener;

    FriendshipAdapter(List<FriendshipModel> friends, FriendshipInteraction listener) {
        this.mFriends = friends;
        mInteractionListener = listener;
    }

    public void setData(List<FriendshipModel> data) {
        this.mFriends = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int  position) {
        holder.bindData(mFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriends == null ? 0 : mFriends.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_name) TextView tvName;
        @BindView(R.id.tv_checkins) TextView tvCheckins;
        @BindView(R.id.im_profile_pic) CircleImageView imProfilePic;

        private FriendshipModel friendshipModel;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(FriendshipModel friendshipModel) {
            this.friendshipModel = friendshipModel;
            GlideApp.with(itemView.getContext()).load(friendshipModel.getUserPic()).into(imProfilePic);
            tvName.setText(friendshipModel.getUserName());
            tvCheckins.setText("Checkins: " + friendshipModel.formatCheckins());
            setFriendshipAction(friendshipModel.getStatus());
        }

        private void setFriendshipAction(FRIEND_STATUS friendStatus) {
            if (friendStatus == FRIEND_STATUS.NONE) {
                itemView.findViewById(R.id.container_status_none).setVisibility(View.VISIBLE);
            } else if (friendStatus == FRIEND_STATUS.PENDING_REQUEST) {
                itemView.findViewById(R.id.container_status_request).setVisibility(View.VISIBLE);
            } else if (friendStatus == FRIEND_STATUS.FRIENDS) {
                itemView.findViewById(R.id.container_status_friend).setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.btn_follow)
        void onFollow() {
            mInteractionListener.onAddFriend(friendshipModel.getUserPk());
        }

        @OnClick(R.id.btn_following)
        void onUnfollow() {
            mInteractionListener.onRemoveFriend(friendshipModel.getUserPk());
        }
    }

    public interface FriendshipInteraction {
       void onAddFriend(long userPk);
       void onRemoveFriend(long userPk);
    }

}