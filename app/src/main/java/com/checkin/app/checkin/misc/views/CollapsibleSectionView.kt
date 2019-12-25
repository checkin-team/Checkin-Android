package com.checkin.app.checkin.misc.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.transition.TransitionManager
import butterknife.OnClick
import com.checkin.app.checkin.R

class CollapsibleSectionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SectionFragmentHolderView(context, attrs, defStyleAttr, R.layout.view_collapsible_section_collapsed) {

    private val expandedCS = ConstraintSet().apply {
        clone(SectionFragmentHolderView(context, attrs, defStyleAttr, R.layout.view_collapsible_section_expanded, fragmentContainerId))
    }
    private val collapsedCS = ConstraintSet().apply { clone(this@CollapsibleSectionView) }
    private var currState = STATE_COLLAPSED
    var listener: SectionListener? = null

    fun collapse(animate: Boolean = true) {
        if (currState == STATE_COLLAPSED) return
        applyState(STATE_COLLAPSED, animate)
        listener?.onCollapse(this)
    }

    fun expand(animate: Boolean = true) {
        if (currState == STATE_EXPANDED) return
        applyState(STATE_EXPANDED, animate)
        listener?.onExpand(this)
    }

    @OnClick(R.id.tv_collapsible_section_text, R.id.im_collapsible_section_dropdown)
    fun onClickHeading() {
        when (currState) {
            STATE_EXPANDED -> collapse()
            STATE_COLLAPSED -> expand()
        }
    }

    private fun applyState(state: Int, animate: Boolean) {
        currState = state
        val color = when (state) {
            STATE_COLLAPSED -> {
                collapsedCS.applyTo(this)
                ContextCompat.getColor(context, R.color.primary_red)
            }
            STATE_EXPANDED -> {
                expandedCS.applyTo(this)
                ContextCompat.getColor(context, R.color.brownish_grey)
            }
            else -> 0
        }
        tvSectionText.setTextColor(color)
        ImageViewCompat.setImageTintList(imDropDown, ColorStateList.valueOf(color))
        if (animate)
            TransitionManager.beginDelayedTransition(this)
    }

    interface SectionListener {
        fun onExpand(view: CollapsibleSectionView)
        fun onCollapse(view: CollapsibleSectionView)
    }

    companion object {
        private const val STATE_COLLAPSED = 0
        private const val STATE_EXPANDED = 1
    }
}