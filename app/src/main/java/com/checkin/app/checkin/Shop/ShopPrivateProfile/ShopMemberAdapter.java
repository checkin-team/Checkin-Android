package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.security.acl.Owner;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopMemberAdapter extends RecyclerView.Adapter<ShopMemberAdapter.ViewHolder> {
    List<MemberModel> mMembers;
    private static OnMemberInteractionListener mItemInteractionListener;
   public ShopMemberAdapter(List<MemberModel> memberModels)
   {
       this.mMembers=memberModels;
   }

    public void setMemberInteractionListener(OnMemberInteractionListener mMemberInteractionListener) {
        ShopMemberAdapter.mItemInteractionListener = mMemberInteractionListener;
    }

    @NonNull
    @Override
    public ShopMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new ShopMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mMembers.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_shop_member;
    }


    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.im_display_pic) ImageView userPic;
        @BindView(R.id.tv_display_name) TextView userName;
        @BindView(R.id.btn_role_edit) Button memberRole;



        ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }

        void bindData(MemberModel member) {
            userName.setText(member.getUser().getDisplayName());
            Glide.with(userName.getContext()).load(member.getUser().getDisplayPic()).into(userPic);
            if(member.isOwner()) memberRole.setText("Owner");
            else if(member.isAdmin()) memberRole.setText("Admin");
            else if(member.isManager()) memberRole.setText("Manager");

            else if(member.isWaiter()) memberRole.setText("Waiter");

            else if(member.isCook()) memberRole.setText("Cook");

            memberRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemInteractionListener.onClickAssign(member,getAdapterPosition());
                }
            });
        }

    }
    public interface OnMemberInteractionListener{
        public void onClickAssign(MemberModel member,int position);

    }
}
