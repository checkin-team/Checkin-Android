package com.checkin.app.checkin.utility;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.MediatorLiveData;

public class SourceMappedLiveData<T> extends MediatorLiveData<T> {
    @NonNull
    private final Function<T, T> mapFunction;

    public SourceMappedLiveData(@NonNull final Function<T, T> mapFunction) {
        this.mapFunction = mapFunction;
    }

    @Override
    public void setValue(T value) {
        super.setValue(mapFunction.apply(value));
    }

    @Override
    public void postValue(T value) {
        super.postValue(mapFunction.apply(value));
    }
}
