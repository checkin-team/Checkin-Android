package com.checkin.app.checkin.Data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Utility.ProblemHandler;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseViewModel extends AndroidViewModel {
    private Application mApplication;
    protected SourceMappedLiveData<Resource<ObjectNode>> mData = createNetworkLiveData();
    ProblemHandler problemHandler;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
    }

    public LiveData<Resource<ObjectNode>> getObservableData() {
        return mData;
    }

    protected <T> SourceMappedLiveData<Resource<T>> createNetworkLiveData() {
        return new SourceMappedLiveData<>(resource -> {
            problemHandler = new ProblemHandler(mApplication);
            if (ProblemHandler.handleProblems(mApplication, resource))
                return null;
            return resource;
        });
    }

    public void resetObservableData() {
        mData.setValue(null);
    }

    public abstract void updateResults();
}
