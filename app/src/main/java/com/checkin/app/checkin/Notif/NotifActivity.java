package com.checkin.app.checkin.Notif;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotifActivity extends AppCompatActivity {

    @BindView(R.id.notifRV) RecyclerView notifRV;
    @BindView(R.id.back) ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("new notification"));
        ButterKnife.bind(this);
        notifRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        notifRV.setAdapter(new NotifAdapter(getNotifs()));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    class NotifAdapter extends RecyclerView.Adapter{
        private static final int TYPE_HEADING = 0;
        private static final int TYPE_NOTIF_MODEL = 1;

        List<Object> notifs;

        public NotifAdapter(List<Object> notifs) {
            this.notifs = notifs;
        }

        class NotifModelViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.profile)ImageView profile;
            @BindView(R.id.action)ImageView action;
            @BindView(R.id.message)TextView message;
            @BindView(R.id.time)TextView time;
            View view;

            public NotifModelViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                view = itemView;
            }
            public void bind(NotifModel notifModel){
                //Glide.with(getApplicationContext()).load(notifModel.getProfileUrl()).into(profile);
                //Glide.with(getApplicationContext()).load(notifModel.getActionUrl()).into(action);
                if(notifModel.getTargetId()%6 == 0)action.setImageResource(R.drawable.noun_question_322256);
                message.setText(notifModel.getMessage());
                time.setText(DateUtils.getRelativeTimeSpanString(notifModel.getTime().getTime()));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"targetId = " + notifModel.getTargetId(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        class NotifHeaderViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.heading) TextView heading;
            public NotifHeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
            public void bind(String heading){
                this.heading.setText(heading);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == TYPE_NOTIF_MODEL){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_item,parent,false);
                return new NotifModelViewHolder(view);
            }
            else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_header,parent,false);
                return new NotifHeaderViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == TYPE_NOTIF_MODEL){
                ((NotifModelViewHolder) holder).bind((NotifModel) notifs.get(position));
            }else {
                ((NotifHeaderViewHolder) holder).bind((String) notifs.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return notifs.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(notifs.get(position) instanceof NotifModel) return TYPE_NOTIF_MODEL;
            else return TYPE_HEADING;
        }
    }

    private List<Object> getNotifs(){
        List<NotifModel> notifs = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            if(i%5==0)
                notifs.add(new NotifModel("Message " + i,new Date(),"profile url","action url",false ,i));
            else notifs.add(new NotifModel("Message " + i,new Date(),"profile url","action url",true ,i));
        }
        return setForNotif(notifs);
    }

    private List<Object> setForNotif(List<NotifModel> notifs){
        List<Object> notifsForDisplay = new ArrayList<>();
        List<Object> seenNotifs = new ArrayList<>();
        List<Object> unseenNotifs = new ArrayList<>();
        for(NotifModel notifModel:notifs){
            if(notifModel.isSeen()) seenNotifs.add(notifModel);
            else unseenNotifs.add(notifModel);
        }
        if(unseenNotifs.size()>0)unseenNotifs.add(0,"New");
        if(seenNotifs.size()>0)seenNotifs.add(0,"Seen");
        notifsForDisplay.addAll(unseenNotifs);
        notifsForDisplay.addAll(seenNotifs);
        return notifsForDisplay;
    }

    private BroadcastReceiver mHandler=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String actionCode=intent.getStringExtra("Action Code");

            String message =intent.getStringExtra("message");
            Toast.makeText(getApplicationContext(),message + ", ACTTIONCODE = " + actionCode,Toast.LENGTH_LONG).show();

            NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }
}
