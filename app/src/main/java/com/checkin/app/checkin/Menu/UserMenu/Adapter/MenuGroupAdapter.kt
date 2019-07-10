package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.animation.ValueAnimator
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuGroupModel
import com.checkin.app.checkin.Menu.UserMenu.Fragment.MenuItemsFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.Utility.Utils
import com.google.android.material.tabs.TabLayout
import java.util.*

class MenuGroupAdapter(private var mGroupList: List<MenuGroupModel>?, private val mFragmentManager: FragmentManager, private val mListener: MenuItemInteraction?, private val mGroupInteractionListener: OnGroupInteractionInterface) : RecyclerView.Adapter<MenuGroupAdapter.GroupViewHolder>() {
    private var mPrevExpandedViewHolder: GroupViewHolder? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mIsSessionActive = true

    val isGroupExpanded: Boolean
        get() = mPrevExpandedViewHolder?.isExpanded ?: false

    init {
        this.setHasStableIds(true)
    }

    fun setSessionActive(value: Boolean) {
        mIsSessionActive = value
    }

    fun setGroupList(mGroupList: List<MenuGroupModel>) {
        this.mGroupList = mGroupList
        notifyDataSetChanged()
    }

    fun hasData(): Boolean {
        return mGroupList?.isEmpty() ?: false
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemCount() = mGroupList?.size ?: 0

    override fun getItemViewType(position: Int) = R.layout.item_as_menu_group_collapsed

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { GroupViewHolder(this) }

    fun getGroupPosition(groupName: String): Int = mGroupList?.run {
        indexOfFirst { groupName.contentEquals(it.name) }
    } ?: 0

    fun contractView() {
        contractView(mPrevExpandedViewHolder)
    }

    fun expandView() {
        expandView(mPrevExpandedViewHolder)
    }

    private fun contractView(groupViewHolder: GroupViewHolder?) {
        if (groupViewHolder != null) {
            groupViewHolder.hide()
            mPrevExpandedViewHolder = null
        }
    }

    private fun expandView(groupViewHolder: GroupViewHolder?) {
        if (groupViewHolder != null) {
            groupViewHolder.show()
            mPrevExpandedViewHolder = groupViewHolder
        }
    }

    override fun onBindViewHolder(groupViewHolder: GroupViewHolder, position: Int) {
        groupViewHolder.bindData(mGroupList!![position])
        if (groupViewHolder.isExpanded) {
            contractView(groupViewHolder)
        }
    }

    interface OnGroupInteractionInterface {
        fun onGroupExpandCollapse(isExpanded: Boolean, groupModel: MenuGroupModel?)
    }

    inner class GroupViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var isExpanded = false

        @BindView(R.id.cl_user_menu_group)
        internal lateinit var containerMenuGroup: ConstraintLayout
        @BindView(R.id.tv_as_menu_group_name)
        internal lateinit var tvGroupName: TextView
        @BindView(R.id.im_as_menu_group_icon)
        internal lateinit var imGroupIcon: ImageView
        @BindView(R.id.tabs_as_menu_sub_groups)
        internal lateinit var vTabs: TabLayout
        @BindView(R.id.pager_as_menu_items)
        internal lateinit var vPager: ViewPager
        @BindView(R.id.container_as_menu_sub_groups)
        internal lateinit var vSubGroupWrapper: ViewGroup
        @BindView(R.id.im_as_menu_drop_down)
        internal lateinit var imDropDown: ImageView

        private var mMenuGroup: MenuGroupModel? = null
        private var mAdapter: SubGroupPagerAdapter? = null

        private lateinit var collapsedCs: ConstraintSet
        private lateinit var expandedCs: ConstraintSet

        init {
            ButterKnife.bind(this, itemView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.setTabsFont(vTabs, itemView.resources.getFont(R.font.arial_rounded_mt_bold))
            }

            itemView.setOnClickListener { view -> tvGroupName.performClick() }
            tvGroupName.setOnClickListener { v ->
                if (this.isExpanded)
                    contractView(this)
                else {
                    contractView()
                    expandView(this)
                }
            }

            collapsedCs = ConstraintSet().apply { clone(containerMenuGroup) }
            expandedCs = ConstraintSet().apply { clone(itemView.context, R.layout.item_as_menu_group_expanded) }

//            val lt = (itemView as ViewGroup).layoutTransition
//            lt.enableTransitionType(LayoutTransition.CHANGING)
//            lt.setDuration(GROUP_ANIMATION_DURATION)
        }

        internal fun bindData(menuGroup: MenuGroupModel) {
            mMenuGroup = menuGroup

            tvGroupName.text = menuGroup.name
            GlideApp.with(itemView).load(menuGroup.icon).into(imGroupIcon)
            mAdapter = SubGroupPagerAdapter(menuGroup)
            vPager.adapter = mAdapter
            if (menuGroup.hasSubGroups()) {
                vTabs.visibility = View.VISIBLE
                vTabs.setupWithViewPager(vPager)
                setupTabIcons()
                vTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    @ColorRes
                    fun getTabColor(position: Int): Int {
                        return if (position == 0) R.color.apple_green else R.color.primary_red
                    }

                    override fun onTabSelected(tab: TabLayout.Tab) {
                        vTabs.setSelectedTabIndicatorColor(vTabs.context.resources.getColor(getTabColor(tab.position)))
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}

                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            } else {
                vTabs.visibility = View.GONE
            }
            vPager.id = adapterPosition + 1
        }

        internal fun show() {
            isExpanded = true
            expandedCs.applyTo(containerMenuGroup)
            val transition = ChangeBounds()
            TransitionManager.beginDelayedTransition(containerMenuGroup, transition)
            mGroupInteractionListener.onGroupExpandCollapse(isExpanded, mMenuGroup)
            val scrollAnim = ValueAnimator.ofInt(1, 2, 3, 4)
            scrollAnim.duration = GROUP_ANIMATION_DURATION
            scrollAnim.addUpdateListener {
                (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(adapterPosition, 20)
            }
            scrollAnim.start()
        }

        internal fun hide() {
            isExpanded = false
            collapsedCs.applyTo(containerMenuGroup)
            TransitionManager.beginDelayedTransition(containerMenuGroup)
            mGroupInteractionListener.onGroupExpandCollapse(isExpanded, mMenuGroup)
        }

        private fun setupTabIcons() {
            var tab: TabLayout.Tab? = vTabs.getTabAt(0)
            if (tab != null) {
                val tabOne = LayoutInflater.from(vTabs.context).inflate(R.layout.tab_menu_subgroup, null)
                val tv = tabOne.findViewById<TextView>(R.id.tv_tab)
                val im = tabOne.findViewById<ImageView>(R.id.im_tab)
                tv.text = "  Veg"
                im.setImageDrawable(mRecyclerView!!.context.resources.getDrawable(R.drawable.ic_veg))
                tab.customView = tabOne
            }

            tab = vTabs.getTabAt(1)
            if (tab != null) {
                val tabTwo = LayoutInflater.from(vTabs.context).inflate(R.layout.tab_menu_subgroup, null)
                val tvTwo = tabTwo.findViewById<TextView>(R.id.tv_tab)
                val imTwo = tabTwo.findViewById<ImageView>(R.id.im_tab)
                tvTwo.text = "  Non-Veg"
                imTwo.setImageDrawable(mRecyclerView!!.context.resources.getDrawable(R.drawable.ic_non_veg))
                tab.customView = tabTwo
            }
        }
    }

    inner class SubGroupPagerAdapter internal constructor(menuGroup: MenuGroupModel) : FragmentStatePagerAdapter(mFragmentManager) {
        private val mListFragment: MutableList<MenuItemsFragment>

        init {
            mListFragment = ArrayList()
            if (menuGroup.hasSubGroups()) {
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.vegItems, mListener, mIsSessionActive))
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.nonVegItems, mListener, mIsSessionActive))
            } else {
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.items, mListener, mIsSessionActive))
            }
        }

        override fun getItem(position: Int): Fragment {
            return mListFragment[position]
        }

        override fun getCount(): Int {
            return mListFragment.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            var title: String? = null
            when (position) {
                0 -> title = "Veg"
                1 -> title = "Non-Veg"
                else -> {
                }
            }
            return title
        }
    }

    companion object {
        private val TAG = MenuGroupAdapter::class.java.simpleName
        private val GROUP_ANIMATION_DURATION = 300L
    }
}