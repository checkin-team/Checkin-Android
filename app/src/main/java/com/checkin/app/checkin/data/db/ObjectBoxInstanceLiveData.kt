package com.checkin.app.checkin.data.db

import androidx.lifecycle.LiveData
import io.objectbox.query.Query
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription

/**
 * A {@link LiveData} which allows to observe changes to only the first result of the given query.
 */
class ObjectBoxInstanceLiveData<T>(private val query: Query<T>) : LiveData<T>() {
    private val listener = DataObserver { data: List<T> ->
        try {
            postValue(data[0])
        } catch (e: Exception) {
            postValue(null)
        }
    }
    private var subscription: DataSubscription? = null

    // called when the LiveData object has an active observer
    override fun onActive() {
        if (subscription == null) {
            subscription = query.subscribe().observer(listener)
        }
    }

    // called when the LiveData object doesn't have any active observers
    override fun onInactive() {
        if (!hasObservers()) {
            subscription?.cancel()
            subscription = null
        }
    }
}