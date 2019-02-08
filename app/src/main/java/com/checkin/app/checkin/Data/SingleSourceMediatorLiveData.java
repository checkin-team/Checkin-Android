package com.checkin.app.checkin.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SingleSourceMediatorLiveData<T> extends MutableLiveData<T> {
    private Source<?> mSource;

    @MainThread
    public <S> void observeSource(@NonNull LiveData<S> source, @NonNull Observer<S> onChanged) {
        if (mSource != null && mSource.mLiveData == source)
            return;
        mSource = new Source<>(source, onChanged);
        if (hasActiveObservers()) {
            mSource.plug();
        }
    }

    @MainThread
    public void removeSources() {
        if (mSource != null) {
            mSource.unplug();
        }
        mSource = null;
    }

    @CallSuper
    @Override
    protected void onActive() {
        if (mSource != null)
            mSource.plug();
    }

    @CallSuper
    @Override
    protected void onInactive() {
        if (mSource != null)
            mSource.unplug();
    }

    private static class Source<V> implements Observer<V> {
        final LiveData<V> mLiveData;
        final Observer<V> mObserver;

        Source(LiveData<V> liveData, final Observer<V> observer) {
            mLiveData = liveData;
            mObserver = observer;
        }

        void plug() {
            mLiveData.observeForever(this);
        }

        void unplug() {
            mLiveData.removeObserver(this);
        }

        @Override
        public void onChanged(@Nullable V v) {
            mObserver.onChanged(v);
        }
    }
}