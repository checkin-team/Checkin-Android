package com.checkin.app.checkin.Data;

import android.app.Application;
import android.app.NotificationManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;

public abstract class BaseViewModel extends AndroidViewModel{
    protected MediatorLiveData<Resource<ObjectNode>> mData = new MediatorLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Resource<ObjectNode>> getObservableData() {
        return mData;
    }

    public void resetObservableData() {
        mData.setValue(null);
    }

    public abstract void updateResults();

}
