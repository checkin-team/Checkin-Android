package com.checkin.app.checkin.utility

import androidx.annotation.CallSuper
import java.lang.ref.WeakReference

open class SingletonHolder<out T, in A>(val creator: (A) -> T) {
    @Volatile
    private var instance: T? = null

    protected open fun createInstance(arg: A) = creator(arg)

    protected fun resetInstance() {
        instance = null
    }

    @CallSuper
    open fun getInstance(arg: A? = null): T = instance ?: synchronized(this) {
        instance ?: createInstance(arg!!).also { instance = it }
    }
}

open class ConflatedSingletonHolder<out T, in A>(creator: (A) -> T) : SingletonHolder<T, A>(creator) {
    @Volatile
    private var lastArg: WeakReference<A>? = null

    override fun createInstance(arg: A): T {
        lastArg = WeakReference(arg)
        return super.createInstance(arg)
    }

    override fun getInstance(arg: A?): T {
        if (arg != null && lastArg != arg) resetInstance()
        return super.getInstance(arg)
    }
}