package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.checkin.app.checkin.Utility.Utils;

import java.text.BreakIterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionChatAdapter extends RecyclerView.Adapter {
    List<ActiveSessionChatModel> mChats;
    Context mContext;
    public static final int CENTER_MSG = 0;
    public static final int RIGHT_MSG = 1;
    public static final int LEFT_MSG = 2;
    private ChatInteraction mChatInterface;

    public ActiveSessionChatAdapter(List<ActiveSessionChatModel> chats, Context context, ChatInteraction chatInterface) {
        mChats = chats;
        mContext = context;
        mChatInterface = chatInterface;
    }

    public void setData(List<ActiveSessionChatModel> data) {
        this.mChats = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RIGHT_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_session_chat_right, parent, false);
                return new ViewHolderRightMsg(view);
            case LEFT_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_session_chat_left, parent, false);
                return new ViewHolderLeftMsg(view);
            case CENTER_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_session_chat_center, parent, false);
                return new ViewHolderCenterMsg(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_CUSTOMER)
            return RIGHT_MSG;
        else if (mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_RESTAURANT)
            return LEFT_MSG;
        else return CENTER_MSG;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_CUSTOMER)
            ((ViewHolderRightMsg) holder).bindData(mChats.get(position));
        else if (mChats.get(position).getSender() == ActiveSessionChatModel.CHATSENDERTYPES.SENDER_RESTAURANT)
            ((ViewHolderLeftMsg) holder).bindData(mChats.get(position));
        else ((ViewHolderCenterMsg) holder).bindData(mChats.get(position));
    }

    @Override
    public int getItemCount() {
        return mChats != null ? mChats.size() : 0;
    }

    class ViewHolderRightMsg extends RecyclerView.ViewHolder {
        ActiveSessionChatModel chat;
        @BindView(R.id.tv_chat_msg)
        TextView tv_chat_msg;
        @BindView(R.id.tv_sender_name)
        TextView tv_sender_name;
        @BindView(R.id.tv_chat_cancel_msg)
        TextView tv_chat_cancel_msg;
        @BindView(R.id.tv_chat_time)
        TextView tv_chat_time;
        @BindView(R.id.tv_chat_cancel_title)
        TextView tv_chat_cancel_title;
        @BindView(R.id.im_msg_delivered)
        ImageView im_msg_delivered;
        @BindView(R.id.im_msg_typing_1)
        ImageView im_msg_typing_1;
        @BindView(R.id.im_msg_typing_2)
        ImageView im_msg_typing_2;
        @BindView(R.id.im_msg_typing_3)
        ImageView im_msg_typing_3;
        @BindView(R.id.ll_order_status_container)
        LinearLayout ll_order_status_container;

        public ViewHolderRightMsg(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

                itemView.setOnLongClickListener(v -> {
                    if ((chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_MENU_ORDER_ITEM ||
                            chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_REQUEST_SERVICE ) &&
                            chat.getStatus() != SessionViewOrdersModel.SESSIONEVENT.CANCELLED) {
                        itemView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_shade));
                        mChatInterface.onLongPress(chat.getType());

                        return true;
                    }
                    return false;
                });


        }

        void bindData(ActiveSessionChatModel chat) {
            this.chat = chat;
            if (chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_MENU_ORDER_ITEM &&
                    chat.getStatus() == SessionViewOrdersModel.SESSIONEVENT.CANCELLED) {
                tv_chat_msg.setVisibility(View.GONE);
                tv_chat_cancel_msg.setText(chat.getMessage());
                tv_chat_cancel_title.setText(mContext.getResources().getString(R.string.status_order_cancelled));
                ll_order_status_container.setVisibility(View.GONE);
                im_msg_delivered.setVisibility(View.VISIBLE);
            } else if (chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_MENU_ORDER_ITEM &&
                    chat.getStatus() == SessionViewOrdersModel.SESSIONEVENT.OPEN) {
                tv_chat_msg.setVisibility(View.VISIBLE);
                ll_order_status_container.setVisibility(View.VISIBLE);
                im_msg_delivered.setVisibility(View.GONE);
                tv_chat_cancel_msg.setVisibility(View.GONE);
                tv_chat_cancel_title.setVisibility(View.GONE);
                im_msg_typing_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
            } else if (chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_MENU_ORDER_ITEM &&
                    chat.getStatus() == SessionViewOrdersModel.SESSIONEVENT.INPROGRESS) {
                tv_chat_msg.setVisibility(View.VISIBLE);
                ll_order_status_container.setVisibility(View.VISIBLE);
                im_msg_delivered.setVisibility(View.GONE);
                tv_chat_cancel_msg.setVisibility(View.GONE);
                tv_chat_cancel_title.setVisibility(View.GONE);
                im_msg_typing_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
                im_msg_typing_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
            } else if (chat.getType() == ActiveSessionChatModel.CHATEVENTTYPE.EVENT_MENU_ORDER_ITEM &&
                    chat.getStatus() == SessionViewOrdersModel.SESSIONEVENT.INPROGRESS) {
                tv_chat_msg.setVisibility(View.VISIBLE);
                ll_order_status_container.setVisibility(View.VISIBLE);
                im_msg_delivered.setVisibility(View.GONE);
                tv_chat_cancel_msg.setVisibility(View.GONE);
                tv_chat_cancel_title.setVisibility(View.GONE);
                im_msg_typing_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
                im_msg_typing_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
                im_msg_typing_3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.oval_primary_red));
            } else {
                tv_chat_msg.setVisibility(View.VISIBLE);
                ll_order_status_container.setVisibility(View.GONE);
                tv_chat_cancel_msg.setVisibility(View.GONE);
                tv_chat_cancel_title.setVisibility(View.GONE);
                im_msg_delivered.setVisibility(View.VISIBLE);
            }
            tv_chat_time.setText(Utils.formatDateTo24HoursTime(chat.getCreated()));
            tv_chat_msg.setText(chat.getMessage());
            tv_sender_name.setText(chat.getUser().getDisplayName());

        }
    }

    class ViewHolderLeftMsg extends RecyclerView.ViewHolder {
        ActiveSessionChatModel chat;
        @BindView(R.id.tv_chat_msg)
        TextView tv_chat_msg;
        @BindView(R.id.tv_sender_name)
        TextView tv_sender_name;
        @BindView(R.id.tv_chat_cancel_msg)
        TextView tv_chat_cancel_msg;
        @BindView(R.id.tv_chat_time)
        TextView tv_chat_time;
        @BindView(R.id.im_msg_delivered)
        ImageView im_msg_delivered;
        @BindView(R.id.im_msg_typing_1)
        ImageView im_msg_typing_1;
        @BindView(R.id.im_msg_typing_2)
        ImageView im_msg_typing_2;
        @BindView(R.id.im_msg_typing_3)
        ImageView im_msg_typing_3;
        @BindView(R.id.ll_order_status_container)
        LinearLayout ll_order_status_container;

        public ViewHolderLeftMsg(View itemView) {
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


        }
    }

    class ViewHolderCenterMsg extends RecyclerView.ViewHolder {
        ActiveSessionChatModel chat;
        @BindView(R.id.tv_chat_msg)
        TextView tv_chat_msg;

        public ViewHolderCenterMsg(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(ActiveSessionChatModel chat) {
            this.chat = chat;
            tv_chat_msg.setText(chat.getMessage());

        }
    }

    public interface ChatInteraction{
        void onLongPress(ActiveSessionChatModel.CHATEVENTTYPE event);
    }

}
