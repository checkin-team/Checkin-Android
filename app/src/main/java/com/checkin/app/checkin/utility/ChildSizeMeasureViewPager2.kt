package com.checkin.app.checkin.utility

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import com.checkin.app.checkin.misc.adapters.BaseFragmentStateAdapter
import com.checkin.app.checkin.misc.adapters.BaseRecyclerViewAdapter

class ChildSizeMeasureViewPager2(private val pager: ViewPager2) : ViewPager2.OnPageChangeCallback(), LifecycleObserver {
    private val adapter = pager.adapter as? BaseFragmentStateAdapter
            ?: pager.adapter as BaseRecyclerViewAdapter

    fun refreshPageSizes() {
        onPageSelected(pager.currentItem)
    }

    override fun onPageSelected(position: Int) {
        val view = when (adapter) {
            is BaseFragmentStateAdapter -> adapter.getViewAtPosition(position)
            is BaseRecyclerViewAdapter -> adapter.getViewAtPosition(position)
            else -> null
        } ?: return
        updatePagerHeightForChild(view)
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
