package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by TAIYAB on 05-05-2018.
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<BaseMessage> mMessageList;

    public MessageListAdapter(Context context, List<BaseMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }
