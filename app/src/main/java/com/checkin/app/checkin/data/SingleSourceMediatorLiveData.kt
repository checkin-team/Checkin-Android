package com.checkin.app.checkin.data

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleSourceMediatorLiveData<T> : MutableLiveData<T>() {
    private var mSource: Source<*>? = null

    @MainThread
    fun <S> observeSource(source: LiveData<S>, onChanged: Observer<S?>) {
        if (mSource?.mLiveData === source) return
        mSource = Source(source, onChanged)
        if (hasActiveObservers()) mSource?.plug()
    }

    @MainThread
    fun removeSources() {
        mSource?.unplug()
        mSource = null
    }

    @CallSuper
    override fun onActive() {
        mSource?.plug()
    }

    @CallSuper
    override fun onInactive() {
        mSource?.unplug()
    }

    private class Source<V> internal constructor(val mLiveData: LiveData<V>, val mObserver: Observer<V?>) : Observer<V?> {
        fun plug() = mLiveData.observeForever(this)

        fun unplug() = mLiveData.removeObserver(this)

        override fun onChanged(v: V?) = mObserver.onChanged(v)
    }
}