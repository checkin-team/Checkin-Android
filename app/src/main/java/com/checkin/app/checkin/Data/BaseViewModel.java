package com.checkin.app.checkin.Data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseViewModel extends AndroidViewModel{
    protected MediatorLiveData<Resource<ObjectNode>> mData = new MediatorLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Resource<ObjectNode>> getObservableData() {
        return mData;
    }

    public abstract void updateResults();
}
