package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.checkin.app.checkin.R;

import java.util.List;

/**
 * Created by ACER on 1/2/2019.
 */

public class ActiveSessionChatTwoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final String TAG = "ActiveSessionChatTwoAda";
    private Context mCtx;


    public ActiveSessionChatTwoAdapter(Context mCtx, List<ActiveSessionChatTwoModel> chatList) {
        this.mCtx = mCtx;
        ChatList = chatList;
    }

    private List<ActiveSessionChatTwoModel> ChatList;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(viewType,null);
       /* if (viewType == R.layout.line) {
            return new LineViewHolder(view);
        }*/
        Log.e(TAG, "onCreateViewHolder: viewholder.........................." );
        return new ActiveSessionChatTwoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ActiveSessionChatTwoViewHolder activeSessionChatTwoViewHolder = ((ActiveSessionChatTwoViewHolder) holder);
            ActiveSessionChatTwoModel activeSessionChatTwoModel = ChatList.get(position);
            activeSessionChatTwoViewHolder.message.setText((activeSessionChatTwoModel.getMessage()));
            activeSessionChatTwoViewHolder.time.setText((activeSessionChatTwoModel.getTime()));
            Log.e(TAG, "onBindViewHolder: bind+++++++++++++++++++++++++++++++++++++");
        } catch (ClassCastException ignored) {

        }
    }
    @Override
    public int getItemViewType(int position) {
       /* if (position % 2 == 0)
            return R.layout.line;*/
        ActiveSessionChatTwoModel messagetyp = (ActiveSessionChatTwoModel)ChatList.get(position);

        if(messagetyp.getId()==1){
            return R.layout.item_active_session_2_left;
        }
        else
            return R.layout.item_active_session_2_right;
    }
    @Override
    public int getItemCount() {
        
        return ChatList.size();
    }

    class ActiveSessionChatTwoViewHolder extends RecyclerView.ViewHolder{

        TextView message,time;

        

        public ActiveSessionChatTwoViewHolder(View itemView) {

            super(itemView);
            message = itemView.findViewById(R.id.tv_as2_chat_message);
            time = itemView.findViewById(R.id.tv_as2_chat_time);

            Log.e(TAG, "ActiveSessionChatTwoViewHolder: holderrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" );
        }
    }
    /*   static class LineViewHolder extends RecyclerView.ViewHolder {
        public LineViewHolder(View itemView) {

            super(itemView);
        }
    }*/
}