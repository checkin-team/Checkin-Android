package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuGroupModel
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Menu.UserMenu.Fragment.MenuItemsHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.model.TrendingDishModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout

class MenuGroupAdapter(private var mGroupList: List<MenuGroupModel>?, private val mListener: MenuItemInteraction?, private val mGroupInteractionListener: OnGroupInteractionInterface?, private val mBestSellerListener: MenuBestSellerAdapter.SessionTrendingDishInteraction?) : HeaderFooterRecyclerViewAdapter() {
    private var mBestsellerItems: List<TrendingDishModel>? = null
    private var mPrevExpandedViewHolder: GroupViewHolder? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mIsSessionActive = true

    private var shouldShowHeader = true

    val isGroupExpanded: Boolean
        get() = mPrevExpandedViewHolder?.isExpanded ?: false

    init {
        this.setHasStableIds(true)
    }

    fun setSessionActive(value: Boolean) {
        mIsSessionActive = value
    }

    fun shouldShowBestSeller(shouldShowBestSeller: Boolean) {
        shouldShowHeader = shouldShowBestSeller
        notifyItemChanged(0)
    }

    fun setGroupList(mGroupList: List<MenuGroupModel>) {
        this.mGroupList = mGroupList
        notifyDataSetChanged()
    }

    fun hasData(): Boolean = mGroupList?.isEmpty() ?: false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView

        mRecyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val v = rv.findChildViewUnder(e.x, e.y) ?: return false
                val holder = rv.findContainingViewHolder(v)
                if (holder != null && holder is HeaderViewHolder) {
                    rv.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }
        })
    }

    fun setBestSellerData(data: List<TrendingDishModel>) {
        this.mBestsellerItems = data
        notifyItemChanged(0)
    }

    override fun useHeader(): Boolean = shouldShowHeader

    override fun useFooter(): Boolean = false

    override fun onCreateHeaderViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder = LayoutInflater.from(parent!!.context).inflate(R.layout.fragment_as_menu_header_bestseller, parent, false).run { HeaderViewHolder(this) }

    override fun onBindHeaderView(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? HeaderViewHolder)?.bindData(mBestsellerItems ?: emptyList())
    }

    override fun onCreateBasicItemViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder = LayoutInflater.from(parent!!.context).inflate(viewType, parent, false).run { GroupViewHolder(this) }

    override fun onBindBasicItemView(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as GroupViewHolder).bindData(mGroupList!![position])
        if (holder.isExpanded) {
            contractView(holder)
        }
    }

    override fun getBasicItemCount() = mGroupList?.size ?: 0

    override fun getBasicItemType(position: Int) = R.layout.item_as_menu_group_collapsed

    internal inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.shimmer_as_menu_bestseller)
        internal lateinit var shimmerLayout: ShimmerFrameLayout
        @BindView(R.id.rv_menu_bestseller)
        internal lateinit var rvBestSeller: RecyclerView

        private val mAdapter: MenuBestSellerAdapter = MenuBestSellerAdapter(mBestSellerListener)

        init {
            ButterKnife.bind(this, itemView)

            val gridLayoutManager = GridLayoutManager(view.context, 2, RecyclerView.VERTICAL, false)
            rvBestSeller.layoutManager = gridLayoutManager
            rvBestSeller.adapter = mAdapter

            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
        }

        fun bindData(data: List<TrendingDishModel>) {
            mAdapter.setData(data)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun getGroupPosition(groupName: String): Int = mGroupList?.run {
        indexOfFirst { groupName.contentEquals(it.name) }
    } ?: 0

    fun getTopExpandedGroupPosition(): Int? = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition().let {
        if (mPrevExpandedViewHolder?.adapterPosition == it) it + 1 else null
    }

    fun getGroupName(idx: Int) = mGroupList?.getOrNull(idx)?.name ?: ""

    fun contractView() {
        contractView(mPrevExpandedViewHolder)
    }

    fun expandView() {
        expandView(mPrevExpandedViewHolder)
    }

    fun contractView(groupViewHolder: GroupViewHolder?) {
        if (groupViewHolder != null) {
            groupViewHolder.hide()
            mPrevExpandedViewHolder = null
        }
    }

    fun expandView(groupViewHolder: GroupViewHolder?) {
        if (groupViewHolder != null) {
            groupViewHolder.show()
            mPrevExpandedViewHolder = groupViewHolder
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

        private var collapsedCs: ConstraintSet
        private var expandedCs: ConstraintSet

        init {
            ButterKnife.bind(this, itemView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.setTabsFont(vTabs, itemView.resources.getFont(R.font.arial_rounded_mt_bold))
            }

            itemView.setOnClickListener { tvGroupName.performClick() }
            tvGroupName.setOnClickListener {
                if (this.isExpanded) contractView(this)
                else {
                    contractView()
                    expandView(this)
                }
            }

            collapsedCs = ConstraintSet().apply { clone(containerMenuGroup) }
            expandedCs = ConstraintSet().apply { clone(itemView.context, R.layout.item_as_menu_group_expanded) }

        }

        internal fun applyState(state: Int, animate: Boolean = true) {
            when (state) {
                STATE_EXPANDED -> {
                    expandedCs.applyTo(containerMenuGroup)
                    val color = itemView.resources.getColor(R.color.primary_red)
                    tvGroupName.setTextColor(color)
                    ImageViewCompat.setImageTintList(imDropDown, ColorStateList.valueOf(color))
                }
                STATE_COLLAPSED -> {
                    collapsedCs.applyTo(containerMenuGroup)
                    val color = itemView.resources.getColor(R.color.brownish_grey)
                    tvGroupName.setTextColor(color)
                    ImageViewCompat.setImageTintList(imDropDown, ColorStateList.valueOf(color))
                }
            }
            if (animate) TransitionManager.beginDelayedTransition(containerMenuGroup)
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
        }

        internal fun show() {
            isExpanded = true
            applyState(STATE_EXPANDED)
            mGroupInteractionListener?.onGroupExpandCollapse(isExpanded, mMenuGroup)
            val scrollAnim = ValueAnimator.ofInt(1, 2, 3, 4)
            scrollAnim.duration = GROUP_ANIMATION_DURATION
            scrollAnim.addUpdateListener {
                (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(adapterPosition, 0)
            }
            scrollAnim.start()
        }

        internal fun hide() {
            isExpanded = false
            applyState(STATE_COLLAPSED)
            mGroupInteractionListener?.onGroupExpandCollapse(isExpanded, mMenuGroup)
        }

        private fun setupTabIcons() {
            var tab: TabLayout.Tab? = vTabs.getTabAt(0)
            if (tab != null) {
                val tabOne = LayoutInflater.from(vTabs.context).inflate(R.layout.tab_menu_subgroup, null)
                val tv = tabOne.findViewById<TextView>(R.id.tv_tab)
                val im = tabOne.findViewById<ImageView>(R.id.im_tab)
                tv.text = "  Veg"
                im.setImageDrawable(mRecyclerView.context.resources.getDrawable(R.drawable.ic_veg))
                tab.customView = tabOne
            }

            tab = vTabs.getTabAt(1)
            if (tab != null) {
                val tabTwo = LayoutInflater.from(vTabs.context).inflate(R.layout.tab_menu_subgroup, null)
                val tvTwo = tabTwo.findViewById<TextView>(R.id.tv_tab)
                val imTwo = tabTwo.findViewById<ImageView>(R.id.im_tab)
                tvTwo.text = "  Non-Veg"
                imTwo.setImageDrawable(mRecyclerView.context.resources.getDrawable(R.drawable.ic_non_veg))
                tab.customView = tabTwo
            }
        }
    }

    inner class SubGroupPagerAdapter internal constructor(menuGroup: MenuGroupModel) : PagerAdapter() {
        private val mListItems: MutableList<List<MenuItemModel>> = ArrayList()

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

        init {
            if (menuGroup.hasSubGroups()) {
                mListItems.add(menuGroup.vegItems)
                mListItems.add(menuGroup.nonVegItems)
            } else {
                mListItems.add(menuGroup.items)
            }
        }

        override fun getCount(): Int = mListItems.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any = MenuItemsHolder(mListItems[position], mListener, container).getView().apply { container.addView(this) }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
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

        private val STATE_COLLAPSED = 0
        private val STATE_EXPANDED = 1
    }
}