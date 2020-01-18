package com.checkin.app.checkin.menu.holders

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.transition.TransitionManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.menu.fragments.MenuItemsHolder
import com.checkin.app.checkin.menu.listeners.MenuItemInteraction
import com.checkin.app.checkin.menu.models.MenuGroupModel
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.google.android.material.tabs.TabLayout

@EpoxyModelClass(layout = R.layout.item_as_menu_group_collapsed)
abstract class UserMenuGroupModelHolder : EpoxyModelWithHolder<UserMenuGroupModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var groupData: MenuGroupModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var itemListener: MenuItemInteraction

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var groupViewListener: ViewGroupInteraction

    @EpoxyAttribute
    lateinit var groupItemsOrderedCounts: Map<Long, Int>

    @EpoxyAttribute
    @JvmField
    var isExpanded = false

    override fun createNewHolder(): Holder = Holder(groupViewListener, itemListener)

    override fun bind(holder: Holder) {
        holder.bindData(groupData)
        holder.updateItemCounts(groupItemsOrderedCounts)
        holder.updateExpansion(isExpanded, animate = false)
    }

    override fun bind(holder: Holder, previouslyBoundModel: EpoxyModel<*>) {
        if (previouslyBoundModel is UserMenuGroupModelHolder) {
            if (groupItemsOrderedCounts != previouslyBoundModel.groupItemsOrderedCounts)
                holder.updateItemCounts(groupItemsOrderedCounts)
            if (isExpanded != previouslyBoundModel.isExpanded)
                holder.updateExpansion(isExpanded, animate = true)
        } else super.bind(holder, previouslyBoundModel)
    }

    class Holder(
            private val groupViewListener: ViewGroupInteraction,
            private val itemListener: MenuItemInteraction
    ) : BaseEpoxyHolder<MenuGroupModel>() {
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

        private lateinit var collapsedCs: ConstraintSet
        private lateinit var expandedCs: ConstraintSet

        private lateinit var mAdapter: SubGroupPagerAdapter
        private lateinit var menuGroup: MenuGroupModel
        private var isExpanded: Boolean = false

        override fun bindView(itemView: View) {
            super.bindView(itemView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.setTabsFont(vTabs, itemView.resources.getFont(R.font.arial_rounded_mt_bold))
            }

            itemView.setOnClickListener { tvGroupName.callOnClick() }
            tvGroupName.setOnClickListener {
                groupViewListener.contractView()
                if (!isExpanded) groupViewListener.expandView(menuGroup)
            }

            collapsedCs = ConstraintSet().apply { clone(containerMenuGroup) }
            expandedCs = ConstraintSet().apply { clone(itemView.context, R.layout.item_as_menu_group_expanded) }
        }

        fun updateItemCounts(itemCounts: Map<Long, Int>) {
            mAdapter.updateCount(itemCounts)
        }

        override fun bindData(data: MenuGroupModel) {
            menuGroup = data

            mAdapter = SubGroupPagerAdapter(data, itemListener)
            tvGroupName.text = data.name
            GlideApp.with(itemView).load(data.icon).into(imGroupIcon)
            vPager.adapter = mAdapter
            if (data.hasSubGroups()) {
                vTabs.visibility = View.VISIBLE
                vTabs.setupWithViewPager(vPager)
                setupTabIcons()
                vTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    @ColorRes
                    private fun getTabColor(position: Int): Int = if (position == 0) R.color.apple_green else R.color.primary_red

                    override fun onTabSelected(tab: TabLayout.Tab) {
                        vTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(context, getTabColor(tab.position)))
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}

                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            } else {
                vTabs.visibility = View.GONE
            }
        }

        private fun show(animate: Boolean) = applyState(STATE_EXPANDED, animate)

        private fun hide(animate: Boolean) = applyState(STATE_COLLAPSED, animate)

        private fun setupTabIcons() {
            var tab: TabLayout.Tab? = vTabs.getTabAt(0)
            if (tab != null) {
                val tabOne = LayoutInflater.from(context).inflate(R.layout.tab_menu_subgroup, null)
                val tv = tabOne.findViewById<TextView>(R.id.tv_tab)
                val im = tabOne.findViewById<ImageView>(R.id.im_tab)
                tv.text = "  Veg"
                im.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg))
                tab.customView = tabOne
            }

            tab = vTabs.getTabAt(1)
            if (tab != null) {
                val tabTwo = LayoutInflater.from(context).inflate(R.layout.tab_menu_subgroup, null)
                val tvTwo = tabTwo.findViewById<TextView>(R.id.tv_tab)
                val imTwo = tabTwo.findViewById<ImageView>(R.id.im_tab)
                tvTwo.text = "  Non-Veg"
                imTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_non_veg))
                tab.customView = tabTwo
            }
        }

        private fun applyState(state: Int, animate: Boolean = true) {
            when (state) {
                STATE_EXPANDED -> {
                    expandedCs.applyTo(containerMenuGroup)
                    val color = ContextCompat.getColor(context, R.color.primary_red)
                    tvGroupName.setTextColor(color)
                    ImageViewCompat.setImageTintList(imDropDown, ColorStateList.valueOf(color))
                }
                STATE_COLLAPSED -> {
                    collapsedCs.applyTo(containerMenuGroup)
                    val color = ContextCompat.getColor(context, R.color.brownish_grey)
                    tvGroupName.setTextColor(color)
                    ImageViewCompat.setImageTintList(imDropDown, ColorStateList.valueOf(color))
                }
            }
            if (animate) TransitionManager.beginDelayedTransition(containerMenuGroup)
        }

        fun updateExpansion(expanded: Boolean, animate: Boolean = true) {
            isExpanded = expanded
            if (isExpanded) {
                show(animate)
                if (menuGroup.hasSubGroups() && vTabs.childCount > 1) setupTabIcons()
            } else hide(animate)
        }
    }

    class SubGroupPagerAdapter internal constructor(
            menuGroup: MenuGroupModel,
            private val itemListener: MenuItemInteraction
    ) : PagerAdapter() {
        private val mListItems: MutableList<List<MenuItemModel>> = ArrayList()
        private val mListHolders: MutableList<MenuItemsHolder> = mutableListOf()

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

        init {
            if (menuGroup.hasSubGroups()) {
                mListItems.add(menuGroup.vegItems)
                mListItems.add(menuGroup.nonVegItems)
            } else {
                mListItems.add(menuGroup.items)
            }
        }

        fun updateCount(itemOrderedCount: Map<Long, Int>?) = mListHolders.forEach { it.setItemCounts(itemOrderedCount) }

        override fun getCount(): Int = mListItems.size

        override fun instantiateItem(container: ViewGroup, position: Int): View = MenuItemsHolder(mListItems[position], itemListener, container).run {
            mListHolders.add(position, this)
            getView().also { container.addView(it) }
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) = container.removeView(obj as View).also {
            if (position < mListHolders.size) mListHolders.removeAt(position)
        }

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "Veg"
            1 -> "Non-Veg"
            else -> null
        }
    }

    companion object {
        private const val STATE_COLLAPSED = 0
        private const val STATE_EXPANDED = 1
    }
}

interface ViewGroupInteraction {
    fun contractView()
    fun expandView(group: MenuGroupModel)
}
