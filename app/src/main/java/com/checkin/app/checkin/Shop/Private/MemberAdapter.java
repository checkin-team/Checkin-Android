package com.checkin.app.checkin.Shop.Private;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<MemberModel> mMembers;
    private OnMemberInteractionListener mItemListener;

    MemberAdapter(List<MemberModel> memberModels) {
        this.mMembers = memberModels;
    }

    public void setItemListener(OnMemberInteractionListener listener) {
        this.mItemListener = listener;
    }

    public void setMembers(List<MemberModel> members) {
        mMembers = members;
        notifyDataSetChanged();
    }

    public void addMember(MemberModel member) {
        int position = mMembers.size();
        mMembers.add(member);
        notifyItemInserted(position);
    }

    public void removeMember(int position) {
        mMembers.remove(position);
        notifyItemRemoved(position);
    }

    public void updateMember(MemberModel member, int position) {
        mMembers.set(position, member);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MemberAdapter.ViewHolder(view);
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
        return mMembers != null ? mMembers.size() : 0;
    }

    public interface OnMemberInteractionListener {
        void onAssignRole(MemberModel member, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_display_pic)
        ImageView imDisplayPic;
        @BindView(R.id.tv_display_name)
        TextView tvDisplayName;
        @BindView(R.id.btn_role_edit)
        Button btnMemberRole;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(MemberModel member) {
            tvDisplayName.setText(member.getUser().getDisplayName());

            String picUrl = member.getUser().getDisplayPic();
            GlideApp.with(itemView)
                    .load(picUrl != null ? picUrl : R.drawable.cover_unknown_male)
                    .into(imDisplayPic);

            String val = "Assign";
            if (member.isOwner()) val = "Owner";
            else if (member.isAdmin()) val = "Admin";
            else if (member.isManager()) val = "Manager";
            else if (member.isWaiter()) val = "Waiter";
            else if (member.isCook()) val = "Cook";
            btnMemberRole.setText(val);

            btnMemberRole.setOnClickListener(v -> mItemListener.onAssignRole(member, getAdapterPosition()));
        }

    }
}
