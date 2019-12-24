package com.checkin.app.checkin.Utility

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

@Suppress("unused")
class LockableBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var swipeEnabled = true

    override fun onInterceptTouchEvent(
            parent: CoordinatorLayout,
            child: V,
            event: MotionEvent
    ): Boolean = if (swipeEnabled) super.onInterceptTouchEvent(parent, child, event) else false

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean = if (swipeEnabled) super.onTouchEvent(parent, child, event) else true

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
    ): Boolean {
        val result = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
        return if (swipeEnabled) {
            result
        } else {
            if (target.canScrollVertically(1) || target.canScrollVertically(-1)) false
            else result
        }
    }

    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
    ) {
        if (swipeEnabled) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }

    override fun onStopNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            target: View,
            type: Int
    ) {
        if (swipeEnabled) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
        }
    }

    override fun onNestedPreFling(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            target: View,
            velocityX: Float,
            velocityY: Float
    ): Boolean = if (swipeEnabled) {
        super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    } else false
}