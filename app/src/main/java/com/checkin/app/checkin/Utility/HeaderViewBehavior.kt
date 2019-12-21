package com.checkin.app.checkin.Utility

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.views.HeaderView
import com.google.android.material.appbar.AppBarLayout


class HeaderViewBehavior(context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<HeaderView>(context, attrs) {
    private var mStartMarginLeft = 0
    private var mEndMarginLeft = 0
    private var mMarginRight = 0
    private var mStartMarginBottom = 0
    private var mTitleStartSize = 0f
    private var mTitleEndSize = 0f
    private var isHide = false

    fun getToolbarHeight(context: Context): Int {
        var result = 0
        val tv = TypedValue()
        if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        }
        return result
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: HeaderView, dependency: View): Boolean = dependency is AppBarLayout

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: HeaderView, dependency: View): Boolean {
        println(child)
        shouldInitProperties(child.context)
        val maxScroll = (dependency as AppBarLayout).totalScrollRange
        val percentage = Math.abs(dependency.getY()) / maxScroll.toFloat()
        var childPosition: Float = ((dependency.getHeight()
                + dependency.getY()) - child.height
                - (getToolbarHeight(child.context) - child.height) * percentage / 2)
        childPosition -= mStartMarginBottom * (1f - percentage)
        val lp = child.layoutParams as CoordinatorLayout.LayoutParams
        if (Math.abs(dependency.getY()) >= maxScroll / 2) {
            val layoutPercentage = (Math.abs(dependency.getY()) - maxScroll / 2) / Math.abs(maxScroll / 2)
            lp.leftMargin = (layoutPercentage * mEndMarginLeft).toInt() + mStartMarginLeft
            child.setTextSize(getTranslationOffset(mTitleStartSize, mTitleEndSize, layoutPercentage))
        } else {
            lp.leftMargin = mStartMarginLeft
        }
        lp.rightMargin = mMarginRight
        child.layoutParams = lp
        child.y = childPosition
        if (isHide && percentage < 1) {
            child.visibility = View.VISIBLE
            isHide = false
        } else if (!isHide && percentage == 1f) {
            child.visibility = View.GONE
            isHide = true
        }
        return true
    }

    protected fun getTranslationOffset(expandedOffset: Float, collapsedOffset: Float, ratio: Float): Float {
        return expandedOffset + ratio * (collapsedOffset - expandedOffset)
    }

    private fun shouldInitProperties(context: Context) {
        if (mStartMarginLeft == 0) {
            mStartMarginLeft = context.resources.getDimensionPixelOffset(R.dimen.spacing_small)
        }
        if (mEndMarginLeft == 0) {
            mEndMarginLeft = context.resources.getDimensionPixelOffset(R.dimen.spacing_large)
        }
        if (mStartMarginBottom == 0) {
            mStartMarginBottom = context.resources.getDimensionPixelOffset(R.dimen.spacing_small)
        }
        if (mMarginRight == 0) {
            mMarginRight = context.resources.getDimensionPixelOffset(R.dimen.spacing_small)
        }
        if (mTitleStartSize == 0f) {
            mTitleEndSize = context.resources.getDimensionPixelSize(R.dimen.font_normal).toFloat()
        }
        if (mTitleStartSize == 0f) {
            mTitleStartSize = context.resources.getDimensionPixelSize(R.dimen.font_small).toFloat()
        }
    }
}