package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuGroupModel
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuGroupAdapter
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.parentFragmentDelegate
import com.checkin.app.checkin.Utility.parentViewModels
import com.checkin.app.checkin.menu.controllers.UserMenuGroupController
import com.checkin.app.checkin.menu.holders.SessionTrendingDishInteraction
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.TrendingDishModel
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import kotlinx.coroutines.launch

class UserMenuGroupsFragment : BaseFragment(), MenuGroupAdapter.OnGroupInteractionInterface {
    override val rootLayout: Int = R.layout.fragment_user_menu_groups

    @BindView(R.id.epoxy_rv_user_menu_groups)
    internal lateinit var epoxyRvMenuGroups: EpoxyRecyclerView
    @BindView(R.id.container_as_menu_current_category)
    internal lateinit var containerCurrentCategory: ViewGroup
    @BindView(R.id.tv_as_menu_current_category)
    internal lateinit var tvCurrentCategory: TextView

    private lateinit var groupController: UserMenuGroupController

    val itemListener: MenuItemInteraction by parentFragmentDelegate()
    val viewModel: UserMenuViewModel by parentViewModels()
    val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEpoxyModels()
        setupObservers()
    }

    private fun setupEpoxyModels() {
        epoxyRvMenuGroups.setHasFixedSize(false)
        ViewCompat.setNestedScrollingEnabled(epoxyRvMenuGroups, false)
        val bestSellerItemSpacing = resources.getDimension(R.dimen.spacing_small).toInt()
        groupController = UserMenuGroupController(bestSellerItemSpacing, itemListener, this, object : SessionTrendingDishInteraction {
            override fun onDishChange(itemModel: TrendingDishModel, changeCount: Int) {
                lifecycleScope.launch {
                    viewModel.getMenuItemById(itemModel.pk)?.let { itemListener.onMenuItemChanged(it, changeCount) }
                }
            }
        })
        epoxyRvMenuGroups.setControllerAndBuildModels(groupController)
    }

    private fun setupObservers() {
        viewModel.recommendedItems.observe(this, Observer {
            it?.also { listResource ->
                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) {
                    groupController.trendingDishes = listResource.data
                }
            }
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
        fun newInstance() = UserMenuGroupsFragment()
    }
}
