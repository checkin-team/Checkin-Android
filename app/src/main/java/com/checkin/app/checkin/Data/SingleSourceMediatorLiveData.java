package com.checkin.app.checkin.Data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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