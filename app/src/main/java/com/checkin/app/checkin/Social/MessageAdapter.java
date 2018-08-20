package com.checkin.app.checkin.Social;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyView> {

    private List<Message> mMessageList;
    private final int VIEW_TYPE_MESSAGE_SENT = 1;
    private final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    MessageAdapter(List<Message> messageList){
        mMessageList = messageList;
    }

    public void addMessage(Message message) {
        mMessageList.add(message);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return (mMessageList.get(position).getSenderId() == 0 ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED);
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_sent, parent, false);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_received, parent, false);
                break;
        }

        return new MyView(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        if (mMessageList != null)
            holder.bindData(mMessageList.get(position));
        else {
            // Data not ready
        }

    }

    @Override
    public int getItemCount()
    {
        if (mMessageList != null)
            return mMessageList.size();
        return 0;
    }

    public void setMessages(List<Message> messages) {
        mMessageList = messages;
        notifyDataSetChanged();
    }

    class MyView extends RecyclerView.ViewHolder {

        TextView tv_message;
        TextView tv_time;

        MyView(View view, int viewType) {
            super(view);
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                tv_message = view.findViewById(R.id.sent_message);
                tv_time = view.findViewById(R.id.sent_time);
            } else {
                tv_message = view.findViewById(R.id.received_message);
                tv_time = view.findViewById(R.id.recieved_time);
            }
        }

        void bindData(Message message) {
            tv_message.setText(message.getMessage());
            tv_time.setText(message.getTime());
        }
    }
}