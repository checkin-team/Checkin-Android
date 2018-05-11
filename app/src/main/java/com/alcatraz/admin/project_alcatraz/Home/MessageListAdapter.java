package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by TAIYAB on 05-05-2018.
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    //private List<BaseMessage> mMessageList;

    public MessageListAdapter(Context context){//}, List<BaseMessage> messageList) {
        mContext = context;
        //mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(final int position) {
        return 1;
    }

    @Override
    public  ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        return new ViewHolder(new View(viewGroup.getContext()));
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }
}