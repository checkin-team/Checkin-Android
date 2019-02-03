package com.checkin.app.checkin.User.Private;

import android.app.Application;
import android.app.NotificationManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitCallAsyncTask;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;
    private MediatorLiveData<Resource<UserModel>> mUserData = new MediatorLiveData<>();

    UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchUserData();
    }

    public void fetchUserData() {
        mUserData.addSource(mRepository.getUser(0), mUserData::setValue);
    }

    public LiveData<Resource<UserModel>> getUserData() {
        return mUserData;
    }

    public void updateProfilePic(File pictureFile, Context context) {
        int notificationId = 201;
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        if (notificationManager == null)
            return;
        // Initial notification show.
        NotificationCompat.Builder builder = MessageUtils.createDownloadNotification(context);
        notificationManager.notify(notificationId, builder.build());
        ProgressRequestBody.UploadCallbacks listener = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                builder.setProgress(100, percentage, false);
                notificationManager.notify(notificationId, builder.build());
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        };
        new RetrofitCallAsyncTask<GenericDetailModel>(listener).execute(mRepository.postUserProfilePic(pictureFile, listener));
    }

    public void postUserData(String firstName, String lastName, String location, String bio) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("locality", location);
        mData.addSource(mRepository.postUserData(data), mData::setValue);
    }
}
