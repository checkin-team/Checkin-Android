package com.checkin.app.checkin.Utility

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate that sets and disposes the fragment's listener by casting it to the fragment's activity.
 */
class ParentActivityDelegate<T>(fragment: Fragment) : BaseParentDelegate<T>(fragment) {
    override fun extractValue(fragment: Fragment): T? = fragment.activity as? T
}

/**
 * Delegate that sets and disposes the fragment's listener by casting it to the fragment's
 * parent fragment.
 */
class ParentFragmentDelegate<T>(fragment: Fragment) : BaseParentDelegate<T>(fragment) {
    override fun extractValue(fragment: Fragment): T? = fragment.parentFragment as? T
}

/**
 * Base delegate that sets and disposes the fragment's listener when the fragment is
 * created and destroyed.
 */
abstract class BaseParentDelegate<T>(private val fragment: Fragment) :
        ReadOnlyProperty<Fragment, T>, LifecycleObserver {

    private var value: T? = null

    init {
        fragment.lifecycle.addObserver(this)
    }

    /**
     * Extract [T] from the fragment. This is called when the fragment is attached to the parent.
     */
    abstract fun extractValue(fragment: Fragment): T?

    // TODO use ON_ATTACH and ON_DETACH events if they are added in new versions of the support
    // library. Issue: https://issuetracker.google.com/issues/111469753.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        value = extractValue(fragment)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        value = null
    }

    operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        throw IllegalStateException("Cannot set value on a ReadOnlyProperty")
    }

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return value!!
    }
}

fun <T> Fragment.parentActivityDelegate(): BaseParentDelegate<T> = ParentActivityDelegate(this)

fun <T> Fragment.parentFragmentDelegate(): BaseParentDelegate<T> = ParentFragmentDelegate(this)
