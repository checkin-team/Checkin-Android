package com.checkin.app.checkin.misc.adapters

import android.util.LongSparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class BaseFragmentStateAdapter : FragmentStateAdapter {
    constructor(fragment: Fragment) : super(fragment)
    constructor(activity: FragmentActivity) : super(activity)

    private val mFragments = LongSparseArray<Fragment>()

    fun getViewAtPosition(position: Int): View? = takeIf { position < itemCount }?.run {
        mFragments.get(position.toLong())?.view
    }

    override fun createFragment(position: Int): Fragment = newFragment(position).also { mFragments.put(getItemId(position), it) }

    abstract fun newFragment(position: Int): Fragment
}