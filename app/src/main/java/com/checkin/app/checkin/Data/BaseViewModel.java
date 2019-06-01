package com.checkin.app.checkin.Data;

import android.app.Application;

import com.checkin.app.checkin.Utility.ProblemHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

public abstract class BaseViewModel extends AndroidViewModel {
    private Application mApplication;
    protected MediatorLiveData<Resource<ObjectNode>> mData = new MediatorLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        registerProblemHandlers();
        mData = registerProblemHandler(mData);
    }

    public LiveData<Resource<ObjectNode>> getObservableData() {
        return mData;
    }

    protected <T> MediatorLiveData<Resource<T>> registerProblemHandler(LiveData<Resource<T>> liveData){
        return ((MediatorLiveData<Resource<T>>) Transformations.map(liveData, resource -> {
            if (ProblemHandler.handleProblems(mApplication, resource))
                return null;
            return resource;
        }));
    }

    public void resetObservableData() {
        mData.setValue(null);
    }

    public abstract void updateResults();

    protected abstract void registerProblemHandlers();

}
