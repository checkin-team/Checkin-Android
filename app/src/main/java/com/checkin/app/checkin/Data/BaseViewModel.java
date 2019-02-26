package com.checkin.app.checkin.Data;

import android.app.Application;

import com.fasterxml.jackson.databind.node.ObjectNode;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public abstract class BaseViewModel extends AndroidViewModel {
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
