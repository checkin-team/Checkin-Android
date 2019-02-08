package com.checkin.app.checkin.Data;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class ObjectBoxInstanceLiveData <T> extends LiveData<T> {
    private final Query<T> query;
    private DataSubscription subscription;

    private final DataObserver<List<T>> listener = data -> {
        try {
            postValue(data.get(0));
        } catch (Exception e) {
            postValue(null);
        }
    };

    public ObjectBoxInstanceLiveData(Query<T> query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        // called when the LiveData object has an active observer
        if (subscription == null) {
            subscription = query.subscribe().observer(listener);
        }
    }

    @Override
    protected void onInactive() {
        // called when the LiveData object doesn't have any active observers
        if (!hasObservers()) {
            subscription.cancel();
            subscription = null;
        }
    }
}
