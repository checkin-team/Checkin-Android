package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnTextChanged
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.controllers.UserMenuItemController
import com.checkin.app.checkin.menu.holders.OnItemInteractionListener
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.BaseCartViewModel
import com.checkin.app.checkin.menu.viewmodels.ScheduledCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MenuDishSearchFragment : BaseFragment(), OnItemInteractionListener, ItemCustomizationBottomSheetFragment.ItemCustomizationInteraction {
    override val rootLayout: Int = R.layout.fragment_menu_item_search

    @BindView(R.id.et_menu_search)
    internal lateinit var etSearch: TextInputEditText

    @BindView(R.id.epoxy_rv_menu_search)
    internal lateinit var epoxyRvItems: EpoxyRecyclerView

    @BindView(R.id.container_menu_search_status)
    internal lateinit var containerStatus: ViewGroup

    @BindView(R.id.container_menu_search_status_not_found)
    internal lateinit var containerNotFound: View

    @BindView(R.id.pb_menu_search_loading)
    internal lateinit var pbLoading: ProgressBar

    private val viewModel: UserMenuViewModel by sharedViewModel()
    private val cartViewModel: BaseCartViewModel by lazy {
        when (arguments?.getInt(KEY_CART_VM_TYPE) ?: 0) {
            0 -> getSharedViewModel(ActiveSessionCartViewModel::class)
            else -> getSharedViewModel(ScheduledCartViewModel::class)
        }
    }
    private val itemController = UserMenuItemController(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        epoxyRvItems.setControllerAndBuildModels(itemController)

        viewModel.filteredMenuItems.observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> if (resource.data != null) handleSuccess(resource.data)
                    Resource.Status.LOADING -> handleLoading()
                    Resource.Status.ERROR_NOT_FOUND -> handleError()
                }
            } ?: handleError()

            // HACK: invalidate needed since UI doesn't refresh until keyboard closed
            epoxyRvItems.invalidate()
            containerStatus.invalidate()
        })
        cartViewModel.itemOrderedCounts.observe(viewLifecycleOwner, Observer {
            it?.also { itemController.orderedCountMap = it }
        })
        etSearch.setText("")
    }

    private fun handleError() {
        // set empty list as search results
        itemController.itemList = emptyList()

        pbLoading.visibility = View.GONE
        containerNotFound.visibility = View.VISIBLE
        containerStatus.visibility = View.VISIBLE
    }

    private fun handleLoading() {
        pbLoading.visibility = View.VISIBLE
        containerNotFound.visibility = View.GONE
        containerStatus.visibility = View.VISIBLE
    }

    private fun handleSuccess(data: List<MenuItemModel>) {
        itemController.itemList = data
        containerStatus.visibility = View.GONE
    }

    @OnTextChanged(R.id.et_menu_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        viewModel.searchMenuItems(et?.toString() ?: "")
    }

    override fun onCustomizationCancel() {
        viewModel.cancelItem()
    }

    override fun onCustomizationDone() {
        order()
    }

    private fun order() = viewModel.currentItem.value?.let {
        cartViewModel.orderItem(it)
        viewModel.resetItem()
    }

    private fun onItemInteraction(item: MenuItemModel) {
        if (item.isComplexItem) ItemCustomizationBottomSheetFragment.newInstance().show(childFragmentManager, ItemCustomizationBottomSheetFragment.FRAGMENT_TAG)
        else order()
    }

    override fun onItemAdded(item: MenuItemModel?): Boolean = item?.let {
        viewModel.newOrderedItem(item)
        onItemInteraction(item)
        true
    } ?: false

    override fun onItemLongPress(item: MenuItemModel): Boolean = true

    override fun onItemChanged(item: MenuItemModel?, count: Int): Boolean = item?.let { menuItem ->
        cartViewModel.updateOrderedItem(item, count)?.let {
            viewModel.setCurrentItem(it)
            this.onItemInteraction(item)
            true
        }
    } ?: onItemAdded(item)

    companion object {
        const val FRAGMENT_TAG = "menu.search"
        private const val KEY_CART_VM_TYPE = "menu.search.cart_vm"

        fun withScheduledCart() = MenuDishSearchFragment().apply {
            arguments = bundleOf(KEY_CART_VM_TYPE to 1)
        }

        fun withAsCart() = MenuDishSearchFragment().apply {
            arguments = bundleOf(KEY_CART_VM_TYPE to 0)
        }
    }
}