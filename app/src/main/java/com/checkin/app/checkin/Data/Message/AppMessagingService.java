package com.checkin.app.checkin.Data.Message;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Data.Converters;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppMessagingService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    ArrayList<MessageModel> notificationString = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
        MessageUtils.createDefaultChannels(mNotificationManager);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> params = remoteMessage.getData();

        if (params == null)
            return;
        MessageModel data;
        try {
            String json = Converters.objectMapper.writeValueAsString(params);
            data = Converters.objectMapper.readValue(json, MessageModel.class);
            Log.e(TAG, data.toString());
        } catch (IOException e) {
            Log.e(TAG, "Couldn't parse FCM remote data.", e);
            return;
        }

        boolean shouldShowNotification = false;
        if (data.shouldTryUpdateUi()) {
            shouldShowNotification = !MessageUtils.sendLocalBroadcast(this, data) && data.shouldShowNotification();
        }

        if (shouldShowNotification) {
//            int notificationId = Constants.getNotificationID();
            int notificationId = 101;

            if(notificationString.size()>0){
                for(int i=0; i<notificationString.size() ; i++){
                    if(notificationString.get(i).getTarget().getPk() == data.getTarget().getPk()){
                        notificationString.add(data);
                        data.showNotification(this, mNotificationManager, notificationId, notificationString);
                        break;
                    }else {
                        data.showNotification(this, mNotificationManager, notificationId++, null);
                        break;
                    }

                }
            }else{
                notificationString.add(data);
                data.showNotification(this, mNotificationManager, notificationId, null);
            }


        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Intent intent = new Intent(getApplicationContext(), DeviceTokenService.class);
        intent.putExtra(DeviceTokenService.KEY_TOKEN, token);
        startService(intent);
    }
}
