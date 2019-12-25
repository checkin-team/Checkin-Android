package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnTextChanged
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.parentFragmentDelegate
import com.checkin.app.checkin.Utility.parentViewModels
import com.checkin.app.checkin.menu.controllers.UserMenuItemController
import com.checkin.app.checkin.menu.holders.OnItemInteractionListener
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseDialogFragment
import com.google.android.material.textfield.TextInputEditText


class MenuDishSearchFragment : BaseDialogFragment(), OnItemInteractionListener {
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

    val viewModel: UserMenuViewModel by parentViewModels()
    val cartViewModel: CartViewModel by parentViewModels()
    val itemController = UserMenuItemController(this)
    val listener: MenuItemInteraction by parentFragmentDelegate()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
//    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvItems.setControllerAndBuildModels(itemController)

        viewModel.filteredMenuItems.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> if (it.data != null) handleSuccess(it.data)
                Resource.Status.LOADING -> handleLoading()
                Resource.Status.ERROR_NOT_FOUND -> handleError()
            }
        })
        cartViewModel.itemOrderedCounts.observe(this, Observer {
            it?.also { itemController.orderedCountMap = it }
        })
        etSearch.setText("")
    }

    private fun handleError() {
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
        if (et != null && et.toString().isNotEmpty()) viewModel.searchMenuItems(et.toString()) else {
            viewModel.resetMenuItems()
            containerStatus.visibility = View.GONE
        }
    }

    override fun onItemAdded(item: MenuItemModel?): Boolean = item?.let {
        listener.onMenuItemAdded(it)
    } ?: false

    override fun onItemLongPress(item: MenuItemModel): Boolean {
        listener.onMenuItemShowInfo(item)
        return true
    }

    override fun onItemChanged(item: MenuItemModel?, count: Int): Boolean = item?.let {
        listener.onMenuItemChanged(it, count)
    } ?: false

    companion object {
        const val FRAGMENT_TAG = "menu.search"
    }
}