package com.alcatraz.admin.project_alcatraz.Social;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Social.ChatDao.BriefChat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<BriefChat> mBriefChats;
    
    public ChatAdapter(List<BriefChat> briefChats) {
        mBriefChats = briefChats;
    }

    public void setBriefChats(@NonNull List<BriefChat> briefChats) {
        mBriefChats = briefChats;
        notifyDataSetChanged();
    }

    public BriefChat getBriefChat(final int position) {
        return mBriefChats.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.chat_brief_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
       holder.bindData(mBriefChats.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mBriefChats != null ? mBriefChats.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.msg_user_name)
        TextView tvUserName;
        @BindView(R.id.msg_timestamp)
        TextView tvTimestamp;
        @BindView(R.id.msg_last)
        TextView tvLastMsg;
        @BindView(R.id.msg_user_photo)
        ImageView imUserPhoto;

        public final static String dateFormat = "hh:mm a";

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(BriefChat briefChat) {
            if (briefChat.lastMessage == null || briefChat.sentAt == null) {
                tvLastMsg.setText("No message sent.");
                tvTimestamp.setText("");
            } else {
                tvLastMsg.setText(briefChat.lastMessage);
                tvTimestamp.setText(DateFormat.format(dateFormat, briefChat.sentAt));
            }
            tvUserName.setText(String.valueOf(briefChat.userName));
            Glide.with(imUserPhoto.getContext()).load(R.drawable.fin)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(imUserPhoto);
        }
    }

}