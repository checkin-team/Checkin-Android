package com.checkin.app.checkin.User.Friendship;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.WebApiService;
import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendshipBroadcastReceiver extends BroadcastReceiver implements Callback<ObjectNode> {
    private static final String TAG = FriendshipBroadcastReceiver.class.getSimpleName();

    private static final String KEY_NOTIFICATION_ID = "notification.id";
    private static final String KEY_REQUEST_PK = "request.pk";
    private static final String KEY_SHOULD_ACCEPT_REQUEST = "request.should_accept";

    public static PendingIntent getRequestAcceptIntent(Context context, int requestPk, int notificationId) {
        Intent intent = new Intent(context, FriendshipBroadcastReceiver.class)
                .putExtra(KEY_REQUEST_PK, requestPk)
                .putExtra(KEY_SHOULD_ACCEPT_REQUEST, true)
                .putExtra(KEY_NOTIFICATION_ID, notificationId);
        return PendingIntent.getBroadcast(context, requestPk, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PendingIntent getRequestRejectIntent(Context context, int requestPk) {
        Intent intent = new Intent(context, FriendshipBroadcastReceiver.class)
                .putExtra(KEY_REQUEST_PK, requestPk)
                .putExtra(KEY_SHOULD_ACCEPT_REQUEST, false);
        return PendingIntent.getBroadcast(context, requestPk, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(intent.getIntExtra(KEY_NOTIFICATION_ID, -1));


        WebApiService webApiService = ApiClient.getApiService(context);
        int requestPk = intent.getIntExtra(KEY_REQUEST_PK, 0);
        boolean shouldAcceptRequest = intent.getBooleanExtra(KEY_SHOULD_ACCEPT_REQUEST, true);
        if (shouldAcceptRequest) {
            webApiService.acceptFriendRequest(String.valueOf(requestPk)).enqueue(this);
        } else {
            webApiService.rejectFriendRequest(String.valueOf(requestPk)).enqueue(this);
        }

        // Close notification
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void processResponse(ApiResponse<ObjectNode> data) {
        if (data.isSuccessful()) {
            Log.e(TAG, "Success: " + (data.getData() != null ? data.getData().toString() : ""));
        } else {
            Log.e(TAG, "Failure: " + data.getErrorMessage());
        }
    }

    @Override
    public void onResponse(@NonNull Call<ObjectNode> call, @NonNull Response<ObjectNode> response) {
        processResponse(new ApiResponse<>(response));
    }

    @Override
    public void onFailure(@NonNull Call<ObjectNode> call, @NonNull Throwable t) {
        processResponse(new ApiResponse<>(t));
    }
}
