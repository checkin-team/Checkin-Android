package com.checkin.app.checkin.Notif;

import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NotifActivity extends AppCompatActivity {

    @BindView(R.id.notifRV) RecyclerView notifRV;
    @BindView(R.id.back) ImageView back;

    private NotifViewModel notifViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.checkin.app.checkin.notifications"));
        ButterKnife.bind(this);
        notifViewModel = ViewModelProviders.of(this,new NotifViewModel.Factory(getApplication())).get(NotifViewModel.class);
        notifRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        NotifAdapter notifAdapter = new NotifAdapter();
        notifRV.setAdapter(notifAdapter);
        //notifAdapter.setNotifs(getNotifs());
        notifViewModel.getNotifModel().observe(this, new Observer<Resource<List<NotifModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<NotifModel>> listResource) {
                notifAdapter.setNotifs(listResource.data);
            }
        });
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

        List<Object> notifs = new ArrayList<>();

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
                        Intent intent = new Intent(getApplicationContext(),notifModel.getActionClass());
                        intent.putExtra(Constants.TARGET_ID,notifModel.getTargetId());
//                        startActivity(intent);
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

        public void setNotifs(List<NotifModel> notifs){
            if(notifs==null) return;
            List<Object> notifsForDisplay = new ArrayList<>();
            notifsForDisplay.add(0,"New");
            long recentNotifTime = System.currentTimeMillis();
            if(notifs.size() > 0){
                recentNotifTime = notifs.get(0).getTime().getTime();
            }
            long range = MILLISECONDS.convert(Constants.NEW_NOTIF_RANGE,DAYS);
            boolean isLaterAdded = false;
            for(NotifModel notifModel:notifs){
                if(recentNotifTime - notifModel.getTime().getTime() < range) notifsForDisplay.add(notifModel);
                else if(!isLaterAdded){
                    notifsForDisplay.add("Later");
                    isLaterAdded = true;
                }else notifsForDisplay.add(notifModel);
            }
            this.notifs =  notifsForDisplay;
            notifyDataSetChanged();
        }
    }

    private List<NotifModel> getNotifs(){
        List<NotifModel> notifs = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            if(i%5==0)
                notifs.add(new NotifModel("Message " + i,new Date(),"profile url","action url",false ,i));
            else notifs.add(new NotifModel("Message " + i,new Date(),"profile url","action url",true ,i));
        }
        return notifs;
    }

    private BroadcastReceiver mHandler=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String message =intent.getStringExtra("message");
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

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
