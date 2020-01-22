package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.parentActivityDelegate
import com.checkin.app.checkin.Utility.parentFragmentDelegate
import com.checkin.app.checkin.Utility.parentViewModels
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.controllers.OnGroupInteraction
import com.checkin.app.checkin.menu.controllers.UserMenuGroupController
import com.checkin.app.checkin.menu.holders.SessionTrendingDishInteraction
import com.checkin.app.checkin.menu.listeners.MenuItemInteraction
import com.checkin.app.checkin.menu.models.MenuGroupModel
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.BaseCartViewModel
import com.checkin.app.checkin.menu.viewmodels.ScheduledCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.TrendingDishModel
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class MenuGroupsFragment : BaseFragment(), OnGroupInteraction {
    override val rootLayout: Int = R.layout.fragment_user_menu_groups

    @BindView(R.id.epoxy_rv_user_menu_groups)
    internal lateinit var epoxyRvMenuGroups: EpoxyRecyclerView
    @BindView(R.id.container_as_menu_current_category)
    internal lateinit var containerCurrentCategory: ViewGroup
    @BindView(R.id.tv_as_menu_current_category)
    internal lateinit var tvCurrentCategory: TextView

    private lateinit var groupController: UserMenuGroupController

    private val itemListener: MenuItemInteraction by parentFragmentDelegate()
    private val viewModel: UserMenuViewModel by parentViewModels()
    private val cartViewModel: BaseCartViewModel by lazy {
        when (arguments?.getInt(KEY_CART_VM_TYPE) ?: 0) {
            0 -> getSharedViewModel(ActiveSessionCartViewModel::class)
            else -> getSharedViewModel(ScheduledCartViewModel::class)
        }
    }
    private val screenListener: MenuGroupScreenInteraction by parentActivityDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEpoxyModels()
        setupObservers()
    }

    private fun setupEpoxyModels() {
        val bestSellerItemSpacing = resources.getDimension(R.dimen.spacing_small).toInt()
        groupController = UserMenuGroupController(bestSellerItemSpacing, itemListener, this, object : SessionTrendingDishInteraction {
            override fun onDishChange(itemModel: TrendingDishModel, changeCount: Int) {
                lifecycleScope.launch {
                    viewModel.getMenuItemById(itemModel.pk)?.let { itemListener.onMenuItemChanged(it, changeCount) }
                }
            }
        })
        epoxyRvMenuGroups.apply {
            setHasFixedSize(false)
            ViewCompat.setNestedScrollingEnabled(this, true)
            setControllerAndBuildModels(groupController)
//            layoutManager = object : LinearLayoutManager(requireContext()) {
//                override fun canScrollVertically(): Boolean = false
//            }
        }
        groupController.addModelBuildListener {
            screenListener.onListBuilt()
            epoxyRvMenuGroups.post {
                groupController.expandedGroupId?.let { groupId ->
                    val pos = groupController.adapter.run {
                        getModelById(groupId)?.let { getModelPosition(it) } ?: 0
                    }
                    (epoxyRvMenuGroups.layoutManager as LinearLayoutManager).findViewByPosition(pos)?.also { screenListener.onExpandGroupView(it) }
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.recommendedItems.observe(this, Observer {
            it?.also { listResource ->
                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) {
                    groupController.trendingDishes = listResource.data
                }
            }
        })
        viewModel.doShowBestseller.observe(this, Observer {
            groupController.doShowBestseller = it == true
        })

        viewModel.menuGroups.observe(this, Observer {
            it?.also { menuGroupResource ->
                if (menuGroupResource.status === Resource.Status.SUCCESS && !menuGroupResource.isCached) {
                    stopRefreshing()
                    setupData(menuGroupResource.data)
                } else if (menuGroupResource.status === Resource.Status.LOADING) {
                    startRefreshing()
                    if (!groupController.hasData) setupData(menuGroupResource.data)
                } else {
                    stopRefreshing()
                    Utils.toast(requireContext(), menuGroupResource.message)
                }
            }
        })

        cartViewModel.itemOrderedCounts.observe(this, Observer {
            it?.also { groupController.orderedCounts = it }
        })
    }

    private fun setupData(data: List<MenuGroupModel>?) = data?.let {
        groupController.menuGroups = it
        containerCurrentCategory.visibility = View.GONE
    }

    override fun onGroupExpandCollapse(isExpanded: Boolean, groupModel: MenuGroupModel?) {
        if (isExpanded) {
            AnimationUtil.fadeInView(containerCurrentCategory)
            tvCurrentCategory.text = groupModel!!.name
        } else {
            AnimationUtil.fadeOutView(containerCurrentCategory)
        }
    }

    companion object {
        private const val KEY_CART_VM_TYPE = "menu.groups.cart_vm"

        fun withScheduledCart() = MenuGroupsFragment().apply {
            arguments = bundleOf(KEY_CART_VM_TYPE to 1)
        }

        fun withAsCart() = MenuGroupsFragment().apply {
            arguments = bundleOf(KEY_CART_VM_TYPE to 0)
        }
    }
}

interface MenuGroupScreenInteraction {
    fun onListBuilt()
    fun onExpandGroupView(view: View)
}