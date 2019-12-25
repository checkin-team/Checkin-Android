package com.checkin.app.checkin.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.checkin.app.checkin.R
import com.google.android.material.tabs.TabLayout
import java.util.*

abstract class BaseFragmentAdapterBottomNav(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mPager: ViewPager? = null
    private var mSelectedPos = -1
    private val selectionHandlers: MutableList<TabSelectionHandler> = ArrayList()

    @DrawableRes
    abstract fun getTabDrawable(position: Int): Int

    @LayoutRes
    open fun getCustomView(position: Int): Int = R.layout.view_tab_bottom_nav

    open fun onSelectPosition(position: Int) = false

    private fun bindTabText(tvTitle: TextView, position: Int) {
        if (getPageTitle(position) != null) {
            tvTitle.text = getPageTitle(position)
            tvTitle.visibility = View.VISIBLE
        } else tvTitle.visibility = View.GONE
    }

    protected open fun bindTabIcon(imIcon: ImageView, position: Int) {
        imIcon.setImageResource(getTabDrawable(position))
    }

    private fun bindCustomView(view: View, position: Int) {
        val tvTitle = view.findViewById<TextView>(R.id.tv_bnav_title)
        val imIcon = view.findViewById<ImageView>(R.id.iv_bnav_icon)
        bindTabText(tvTitle, position)
        bindTabIcon(imIcon, position)
    }

    fun setupWithTab(tabLayout: TabLayout, viewPager: ViewPager) {
        mPager = viewPager
        val count = count
        if (count > 0) mSelectedPos = 0
        for (pos in 0 until count) {
            val itemView = LayoutInflater.from(tabLayout.context).inflate(getCustomView(pos), null, false)
            bindCustomView(itemView, pos)
            selectionHandlers.add(TabSelectionHandler(pos, itemView))
            val tab = tabLayout.getTabAt(pos)
            if (tab != null) {
                tab.customView = itemView
            }
        }
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (onSelectPosition(position)) return
                if (mSelectedPos != -1)
                    selectionHandlers[mSelectedPos].deselect()
                mSelectedPos = position
                selectionHandlers[mSelectedPos].select()
            }
        })
    }

    protected open fun onTabClick(position: Int) {
        mPager!!.setCurrentItem(position, true)
    }

    private inner class TabSelectionHandler internal constructor(private val mPos: Int, private val mView: View) : View.OnClickListener {
        override fun onClick(v: View) {
            onTabClick(mPos)
        }

        fun deselect() {
            mView.isSelected = false
        }

        fun select() {
            mView.isSelected = true
        }

        init {
            mView.setOnClickListener(this)
        }
    }
}