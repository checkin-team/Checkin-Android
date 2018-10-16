package com.checkin.app.checkin.User.PrivateProfile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomFollowersAdapter extends RecyclerView.Adapter<CustomFollowersAdapter.MyViewHolder>  {
    // declaring some fields.
    private List<FriendshipModel> mFriends;
    private FriendsInteraction interactionListener;
    // A constructor.
    public CustomFollowersAdapter(List<FriendshipModel> arrayList, FriendsInteraction listener) {
        this.mFriends = arrayList;
        interactionListener = listener;
    }

    public void setData(List<FriendshipModel> data) {
        this.mFriends = data;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_userName) TextView tvName;
        @BindView(R.id.tv_checkins) TextView tvCheckins;
        @BindView(R.id.im_profilePic) CircleImageView imProfilePic;
        @BindView(R.id.btn_unfollow)ImageButton btnUnfollow;
        @BindView(R.id.container_btn_follow) ImageButton btnFollow;
        @BindView(R.id.container_btn_following)ImageButton btnFollowing;
        @BindView(R.id.container_btn_requested) ImageButton btnRequested;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(FriendshipModel friendshipModel) {
            GlideApp.with(itemView.getContext()).load(friendshipModel.getUserPic()).into(imProfilePic);
            tvName.setText(friendshipModel.getUserName());
            tvCheckins.setText("Checkins " + friendshipModel.formatCheckins());
            btnUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interactionListener.onUnfollowConnection(friendshipModel);
                }
            });
//         TODO   iconChanged(friendshipModel.setUserStatus());
        }

            public void iconChanged (FriendshipModel.FRIEND_STATUS status){
                if (status == FriendshipModel.FRIEND_STATUS.FRIEND)
                    btnFollowing.setVisibility(View.VISIBLE);
                btnFollowing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                if (status == FriendshipModel.FRIEND_STATUS.BLOCKED)
                    btnRequested.setVisibility(View.VISIBLE);
                btnRequested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                if (status == FriendshipModel.FRIEND_STATUS.REQUESTED)
                    btnRequested.setVisibility(View.VISIBLE);
                btnRequested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                if (status == FriendshipModel.FRIEND_STATUS.NONE)
                    btnFollow.setImageResource(R.drawable.btn_follow);
                btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//              TODO          interactionListener.onRequestAccepted();
                    }
                });
            }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int   viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.followers_list,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int  position) {
        holder.bindData(mFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriends == null ? 0 : mFriends.size();
    }
    public interface FriendsInteraction{
       void onUnfollowConnection(FriendshipModel userModel);
       void onRequestAccepted(FriendshipModel userModel);
    }
}