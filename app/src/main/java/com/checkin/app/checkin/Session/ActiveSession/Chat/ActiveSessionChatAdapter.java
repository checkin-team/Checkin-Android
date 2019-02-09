package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionChatAdapter extends RecyclerView.Adapter {
    private List<SessionChatModel> mChats;
    private SessionChatInteraction mListener;
    @Nullable
    private ViewHolderMessageSent mSelectedViewHolder = null;

    public ActiveSessionChatAdapter(List<SessionChatModel> chats, SessionChatInteraction listener) {
        mChats = chats;
        mListener = listener;
    }

    public void setData(List<SessionChatModel> data) {
        this.mChats = data;
        notifyDataSetChanged();
    }

    @Nullable
    public SessionChatModel getSelectedEvent() {
        if (mSelectedViewHolder != null) {
            return mSelectedViewHolder.chatModel;
        }
        return null;
    }

    public void resetSelectedEvent() {
        if (mSelectedViewHolder != null) {
            mSelectedViewHolder.itemView.setSelected(false);
            mSelectedViewHolder = null;
            mListener.onSelectionChange(null);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.item_active_session_chat_center)
            return new ViewHolderMessageInfo(view);
        else return new ViewHolderMessageSent(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mChats.get(position).getSender() == SessionChatModel.CHAT_SENDER_TYPE.SENDER_CUSTOMER)
            return R.layout.item_active_session_chat_right;
        else if (mChats.get(position).getSender() == SessionChatModel.CHAT_SENDER_TYPE.SENDER_RESTAURANT)
            return R.layout.item_active_session_chat_left;
        else return R.layout.item_active_session_chat_center;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        SessionChatModel data = mChats.get(position);
        if (data.getSender() == SessionChatModel.CHAT_SENDER_TYPE.SENDER_NONE)
            ((ViewHolderMessageInfo) holder).bindData(data);
        else
            ((ViewHolderMessageSent) holder).bindData(data);
    }

    @Override
    public int getItemCount() {
        return mChats != null ? mChats.size() : 0;
    }

    class ViewHolderMessageSent extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_chat_msg)
        TextView tvChatMsg;
        @BindView(R.id.tv_chat_sender_name)
        TextView tvSenderName;
        @BindView(R.id.tv_chat_concern_msg)
        TextView tvChatConcernMsg;
        @BindView(R.id.tv_chat_time)
        TextView tvChatTime;
        @BindView(R.id.im_chat_status_open)
        ImageView imStatusOpen;
        @BindView(R.id.im_chat_status_progress)
        ImageView imStatusProgress;
        @BindView(R.id.im_chat_status_done)
        ImageView imStatusDone;
        @BindView(R.id.ll_order_status_container)
        ViewGroup containerOrderStatus;

        private SessionChatModel chatModel;

        ViewHolderMessageSent(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnLongClickListener(v -> {
                if (this.chatModel.allowConcernRaise()) {
                    if (mSelectedViewHolder != null) {
                        mSelectedViewHolder.itemView.setSelected(false);
                    }
                    this.itemView.setSelected(true);
                    mSelectedViewHolder = this;
                    mListener.onSelectionChange(this.chatModel);
                    return true;
                }
                return false;
            });
            itemView.setOnClickListener(v -> {
                if (mSelectedViewHolder != null) {
                    mSelectedViewHolder.itemView.setSelected(false);
                    mSelectedViewHolder = null;
                    mListener.onSelectionChange(null);
                }
            });
        }

        private void resetLayout() {
            itemView.setSelected(mSelectedViewHolder == this);
            tvChatConcernMsg.setVisibility(View.GONE);
            containerOrderStatus.setVisibility(View.GONE);
        }

        void bindData(SessionChatModel chat) {
            this.chatModel = chat;

            resetLayout();
            if (chat.getUser() != null)
                tvSenderName.setText(chat.getUser().getDisplayName());
            else if (chat.getSender() == SessionChatModel.CHAT_SENDER_TYPE.SENDER_RESTAURANT)
                tvSenderName.setText("Restaurant");
            tvChatTime.setText(chat.formatEventTime());


            imStatusDone.setActivated(chat.getStatus().tag >= CHAT_STATUS_TYPE.DONE.tag);
            imStatusProgress.setActivated(chat.getStatus().tag >= CHAT_STATUS_TYPE.IN_PROGRESS.tag);
            imStatusOpen.setActivated(chat.getStatus().tag >= CHAT_STATUS_TYPE.OPEN.tag);

            switch (chat.getType()) {
                case EVENT_CONCERN:
                case EVENT_MENU_ORDER_ITEM:
                    if (chat.getStatus() == CHAT_STATUS_TYPE.CANCELLED) {
                        tvChatMsg.setText(itemView.getContext().getResources().getString(R.string.status_order_cancelled));
                        tvChatConcernMsg.setText(chat.getMessage());
                        tvChatConcernMsg.setVisibility(View.VISIBLE);
                    } else {
                        if (chat.getType() == CHAT_EVENT_TYPE.EVENT_CONCERN)
                            tvChatMsg.setText("Concern raised.");
                        else
                            tvChatMsg.setText(chat.getMessage());
                        if (chat.getData().getEvent() != null) {
                            tvChatConcernMsg.setText(chat.getData().getEvent().getMessage());
                            tvChatConcernMsg.setVisibility(View.VISIBLE);
                        }
                    }
                    if (chat.getType() == CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM) {
                        containerOrderStatus.setVisibility(View.VISIBLE);
                    }
                    break;
                case EVENT_REQUEST_CHECKOUT:
                case EVENT_REQUEST_SERVICE:
                    containerOrderStatus.setVisibility(View.VISIBLE);
                    tvChatMsg.setText(chat.getMessage());
                    break;
                default:
                    tvChatMsg.setText(chat.getMessage());
            }
        }
    }

    class ViewHolderMessageInfo extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_chat_msg)
        TextView tvChatMsg;

        ViewHolderMessageInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(SessionChatModel chat) {
            tvChatMsg.setText(chat.getMessage());
        }
    }

    public interface SessionChatInteraction {
        void onSelectionChange(@Nullable SessionChatModel chatModel);
    }
}
