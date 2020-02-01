package com.checkin.app.checkin.misc.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by shivanshs9 on 11/5/18.
 */
class DynamicSwipableViewPager @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private var mEnabled = true

    override fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mEnabled && super.onInterceptTouchEvent(event)
    }
}