package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuGroupModel
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuBestSellerAdapter
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuGroupAdapter
import com.checkin.app.checkin.Menu.UserMenu.MenuViewModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.TrendingDishModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import kotlinx.coroutines.launch

class MenuGroupsFragment : BaseFragment(), MenuGroupAdapter.OnGroupInteractionInterface {

    @BindView(R.id.rv_menu_groups)
    internal lateinit var rvGroupsList: RecyclerView
    @BindView(R.id.container_as_menu_current_category)
    internal lateinit var containerCurrentCategory: ViewGroup
    @BindView(R.id.tv_as_menu_current_category)
    internal lateinit var tvCurrentCategory: TextView
    @BindView(R.id.shimmer_as_menu_group)
    internal lateinit var shimmerMenu: ShimmerFrameLayout
    @BindView(R.id.sr_session_menu)
    internal lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mViewModel: MenuViewModel
    private lateinit var mAdapter: MenuGroupAdapter
    private var mListener: MenuItemInteraction? = null
    private var mIsSessionActive = true

    private val isGroupExpanded: Boolean
        get() = mAdapter.isGroupExpanded

    override val rootLayout: Int
        get() = R.layout.fragment_as_menu_group

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupGroupRecycler()
        initRefreshScreen(R.id.sr_session_menu)

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel::class.java)

        mViewModel.recommendedItems.observe(this, Observer {
            it?.let { listResource ->

                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) {
                    mAdapter.setBestSellerData(listResource.data)
                }
            }
        })

        mViewModel.menuGroups.observe(this, Observer {
            it?.let { menuGroupResource ->
                if (menuGroupResource.status === Resource.Status.SUCCESS && !menuGroupResource.isCached) {
                    stopRefreshing()
                    setupData(menuGroupResource.data)
                } else if (menuGroupResource.status === Resource.Status.LOADING) {
                    startRefreshing()
                    if (!mAdapter.hasData() && menuGroupResource.data != null)
                        setupData(menuGroupResource.data)
                } else {
                    stopRefreshing()
                    Utils.toast(requireContext(), menuGroupResource.message)
                }
            }
        })

        mViewModel.currentItem.observe(this, Observer {
            it?.let { orderedItem ->
                //                val holder = orderedItem.itemModel.asItemHolder
//
//                if (holder != null && holder.menuItem === orderedItem.itemModel) {
//                    holder.changeQuantity(mViewModel.getOrderedCount(orderedItem.itemModel) + orderedItem.changeCount)
//                }
            }
        })

        mViewModel.filteredString.observe(this, Observer { mAdapter.shouldShowBestSeller(it == null) })

        mViewModel.getSelectedCategory().observe(this, Observer { if (it != null) mAdapter.shouldShowBestSeller(it.equals("default", ignoreCase = true)) })
    }

    private fun setupData(data: List<MenuGroupModel>?) {
        if (data == null) return
        mAdapter.setGroupList(data)
        if (shimmerMenu.visibility == View.VISIBLE) {
            shimmerMenu.stopShimmer()
            shimmerMenu.visibility = View.GONE
        }
        containerCurrentCategory.visibility = View.GONE
    }

    private fun setupGroupRecycler() {
        rvGroupsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        mAdapter = MenuGroupAdapter(null, mListener, this, object : MenuBestSellerAdapter.SessionTrendingDishInteraction {
            override fun onDishClick(itemModel: TrendingDishModel) {
                lifecycleScope.launch {
                    mViewModel.getMenuItemById(itemModel.pk)?.let { mListener?.onMenuItemAdded(it) }
                }
            }
        })
        mAdapter.setSessionActive(mIsSessionActive)
        rvGroupsList.adapter = mAdapter

        rvGroupsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (view != null) {
                    val topIndex = mAdapter.getTopExpandedGroupPosition()
                    if (topIndex != null) {
                        val fullHeight = view!!.height
                        val view = rvGroupsList.layoutManager!!.findViewByPosition(topIndex)
                        val groupHeight = view?.height ?: 0
                        if (groupHeight < fullHeight)
                            containerCurrentCategory.visibility = View.GONE
                        else {
                            containerCurrentCategory.visibility = View.VISIBLE
                            if (topIndex != tvCurrentCategory.id)
                                tvCurrentCategory.text = mAdapter.getGroupName(topIndex)
                        }
                    } else
                        containerCurrentCategory.visibility = View.GONE
                }
            }
        })
    }

    override fun onBackPressed(): Boolean {
        if (isGroupExpanded) {
            mAdapter.contractView()
            return true
        }
        return false
    }

    fun scrollToGroup(title: String) {
        val pos = mAdapter.getGroupPosition(title)
        if (rvGroupsList.layoutManager != null) {
            val v = rvGroupsList.layoutManager!!.findViewByPosition(pos)
            if (v == null)
                rvGroupsList.smoothScrollToPosition(pos)
            else {
                mAdapter.contractView()
                mAdapter.expandView(rvGroupsList.getChildViewHolder(v) as MenuGroupAdapter.GroupViewHolder)
            }
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        mViewModel.updateResults()
    }

    override fun onGroupExpandCollapse(isExpanded: Boolean, groupModel: MenuGroupModel?) {
        if (isExpanded) {
            AnimationUtil.fadeInView(containerCurrentCategory)
            tvCurrentCategory.text = groupModel!!.name
        } else {
            AnimationUtil.fadeOutView(containerCurrentCategory)
        }
    }

    @OnClick(R.id.container_as_menu_current_category)
    fun onStickyGroup() {
        mAdapter.contractView()
    }

    enum class SESSION_STATUS {
        ACTIVE, INACTIVE
    }

    companion object {
        val KEY_SESSION_STATUS = "menu.status"

        fun newInstance(sessionStatus: SESSION_STATUS, listener: MenuItemInteraction): MenuGroupsFragment {
            val fragment = MenuGroupsFragment()
            fragment.mIsSessionActive = sessionStatus == SESSION_STATUS.ACTIVE
            fragment.mListener = listener
            return fragment
        }
    }
}