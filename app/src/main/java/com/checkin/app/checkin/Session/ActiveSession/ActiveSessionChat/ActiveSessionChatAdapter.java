package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionChatAdapter extends RecyclerView.Adapter<ActiveSessionChatAdapter.ViewHolder> {
    List<ActiveSessionChatModel> mChats;
    Context mContext;

    public ActiveSessionChatAdapter(List<ActiveSessionChatModel> chats, Context context){
        mChats = chats;
        mContext = context;
    }

    public void setData(List<ActiveSessionChatModel> data) {
        this.mChats = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_CUSTOMER) return R.layout.item_active_session_chat_right;
        else if(mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_RESTAURANT) return R.layout.item_active_session_chat_left;
        else return R.layout.item_active_session_chat_left;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mChats.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ActiveSessionChatModel chat;
        @BindView(R.id.tv_chat_msg) TextView tv_chat_msg;
        @BindView(R.id.tv_sender_name) TextView tv_sender_name;
        @BindView(R.id.tv_chat_cancel_title) TextView tv_chat_cancel_title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(v -> {
                itemView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_shade));
                return false;
            });
        }

        void bindData(ActiveSessionChatModel chat) {
            this.chat = chat;
            tv_chat_msg.setText(chat.getMessage());
            tv_sender_name.setText(chat.getUser().getDisplayName());
            tv_chat_cancel_title.setVisibility(View.GONE);

        }
    }
}
