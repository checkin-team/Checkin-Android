package com.checkin.app.checkin.misc.fragments

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.checkin.app.checkin.R

abstract class BaseFragmentNullBehavior : BaseFragment() {
    private var behavior: CoordinatorLayout.Behavior<View>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (behavior != null) return

        activity?.findViewById<FrameLayout>(R.id.frg_container_activity)?.let { view ->
            view.layoutParams?.let {
                if (it is CoordinatorLayout.LayoutParams) {
                    behavior = it.behavior
                    it.behavior = null
                    view.layoutParams = it
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (behavior == null) return

        activity?.findViewById<FrameLayout>(R.id.frg_container_activity)?.let { view ->
            view.layoutParams?.let {
                if (it is CoordinatorLayout.LayoutParams) {
                    it.behavior = behavior
                    behavior = null
                    view.layoutParams = it
                }
            }
        }
    }
}