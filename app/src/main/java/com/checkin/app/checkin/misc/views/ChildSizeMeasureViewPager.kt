package com.checkin.app.checkin.misc.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class ChildSizeMeasureViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    init {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                requestLayout()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getChildHeight(currentItem, widthMeasureSpec), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun getChildHeight(pos: Int, widthMeasureSpec: Int): Int = getChildAt(pos)?.run {
        measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        measuredHeight
    } ?: 0
}