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
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.*
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.controllers.UserMenuItemController
import com.checkin.app.checkin.menu.listeners.MenuItemInteraction
import com.checkin.app.checkin.menu.models.MenuGroupModel
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.misc.adapters.BaseRecyclerViewAdapter
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.utility.ChildSizeMeasureViewPager2
import com.checkin.app.checkin.utility.GlideApp
import com.checkin.app.checkin.utility.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
        internal lateinit var vPager: ViewPager2
        @BindView(R.id.container_as_menu_sub_groups)
        internal lateinit var vSubGroupWrapper: ViewGroup
        @BindView(R.id.im_as_menu_drop_down)
        internal lateinit var imDropDown: ImageView

        private lateinit var collapsedCs: ConstraintSet
        private lateinit var expandedCs: ConstraintSet

        private lateinit var mAdapter: SubGroupPagerAdapter
        private lateinit var menuGroup: MenuGroupModel
        private var itemOrderedCount: Map<Long, Int>? = null
        private var isExpanded: Boolean = false
        private val childSizeUtil by lazy { ChildSizeMeasureViewPager2(vPager) }

        override fun bindView(itemView: View) {
            super.bindView(itemView)

            mAdapter = SubGroupPagerAdapter(itemListener)
            vPager.adapter = mAdapter
            vPager.registerOnPageChangeCallback(childSizeUtil)

            TabLayoutMediator(vTabs, vPager) { tab, pos ->
                val tabOne = LayoutInflater.from(context).inflate(R.layout.tab_menu_subgroup, null)
                val tv = tabOne.findViewById<TextView>(R.id.tv_tab)
                val im = tabOne.findViewById<ImageView>(R.id.im_tab)
                when (pos) {
                    0 -> {
                        tv.text = "  Veg"
                        im.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg))
                    }
                    1 -> {
                        tv.text = "  Non-Veg"
                        im.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_non_veg))
                    }
                }
                tab.customView = tabOne
            }.attach()

            vTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @ColorRes
                private fun getTabColor(position: Int): Int = if (position == 0) R.color.apple_green else R.color.primary_red

                override fun onTabSelected(tab: TabLayout.Tab) {
                    vTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(context, getTabColor(tab.position)))
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

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
            itemOrderedCount = itemCounts
            mAdapter.updateCount(itemCounts)
        }

        override fun bindData(data: MenuGroupModel) {
            menuGroup = data

            mAdapter.setData(data)
            itemOrderedCount?.let { mAdapter.updateCount(it) }
            tvGroupName.text = data.name
            GlideApp.with(itemView).load(data.icon).into(imGroupIcon)
            vTabs.visibility = if (data.hasSubGroups()) View.VISIBLE else View.GONE
        }

        private fun show(animate: Boolean) = applyState(STATE_EXPANDED, animate)

        private fun hide(animate: Boolean) = applyState(STATE_COLLAPSED, animate)

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
                childSizeUtil.refreshPageSizes()
            } else hide(animate)
        }
    }

    class SubGroupPagerAdapter(private val itemListener: MenuItemInteraction?
    ) : BaseRecyclerViewAdapter<SubGroupPagerAdapter.ViewHolder>() {
        private val mListItems: MutableList<List<MenuItemModel>> = ArrayList()
        private var mItemOrderedCount: Map<Long, Int>? = null

        fun updateCount(itemOrderedCount: Map<Long, Int>?) {
            mItemOrderedCount = itemOrderedCount
            notifyDataSetChanged()
        }

        fun setData(menuGroup: MenuGroupModel) {
            mListItems.clear()
            mItemOrderedCount = null
            if (menuGroup.hasSubGroups()) {
                mListItems.add(menuGroup.vegItems)
                mListItems.add(menuGroup.nonVegItems)
            } else {
                mListItems.add(menuGroup.items)
            }
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int = R.layout.fragment_as_menu_items

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = onCreateView(parent, viewType).run { ViewHolder(this) }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindData(mListItems[position])

        override fun getItemCount(): Int = mListItems.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnItemInteractionListener {
            @BindView(R.id.epoxy_rv_menu_items)
            internal lateinit var rvMenuItems: EpoxyRecyclerView

            private val controller = UserMenuItemController(this)

            init {
                ButterKnife.bind(this, itemView)

                rvMenuItems.apply {
                    isNestedScrollingEnabled = false
                    setHasFixedSize(false)
                    setControllerAndBuildModels(controller)
                }
            }

            fun bindData(items: List<MenuItemModel>) {
                controller.itemList = items

                mItemOrderedCount?.let { controller.orderedCountMap = it }
            }

            override fun onItemAdded(item: MenuItemModel?): Boolean = item?.let {
                itemListener?.onMenuItemAdded(it)
            } ?: false

            override fun onItemLongPress(item: MenuItemModel): Boolean = itemListener?.run {
                onMenuItemShowInfo(item)
                true
            } ?: false

            override fun onItemChanged(item: MenuItemModel?, count: Int): Boolean = item?.let {
                itemListener?.onMenuItemChanged(it, count)
            } ?: false
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
