package com.checkin.app.checkin.utility

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import com.checkin.app.checkin.misc.adapters.BaseFragmentStateAdapter

class ChildSizeMeasureViewPager2(private val pager: ViewPager2) : ViewPager2.OnPageChangeCallback(), LifecycleObserver {
    private val adapter = pager.adapter as BaseFragmentStateAdapter

    fun refreshPageSizes() {
        adapter.getViewAtPosition(pager.currentItem)?.let { updatePagerHeightForChild(it) }
    }

    override fun onPageSelected(position: Int) {
        adapter.getViewAtPosition(position)?.let { updatePagerHeightForChild(it) }
    }

    private fun updatePagerHeightForChild(view: View) {
        view.post {
            val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)

            if (pager.layoutParams.height != view.measuredHeight) {
                pager.layoutParams = (pager.layoutParams).also { lp -> lp.height = view.measuredHeight }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        pager.registerOnPageChangeCallback(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestory() {
        pager.unregisterOnPageChangeCallback(this)
    }
}
