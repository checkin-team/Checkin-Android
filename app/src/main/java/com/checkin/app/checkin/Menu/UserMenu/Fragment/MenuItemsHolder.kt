package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.controllers.UserMenuItemController
import com.checkin.app.checkin.menu.holders.OnItemInteractionListener

class MenuItemsHolder(
        menuItems: List<MenuItemModel>,
        private var mListener: MenuItemInteraction?,
        container: ViewGroup
) : OnItemInteractionListener {
    private val mView: View = LayoutInflater.from(container.context).inflate(R.layout.fragment_as_menu_items, container, false)

    private val controller = UserMenuItemController(this)

    @BindView(R.id.epoxy_rv_menu_items)
    internal lateinit var rvMenuItems: EpoxyRecyclerView

    init {
        ButterKnife.bind(this, mView)

        controller.itemList = menuItems

        rvMenuItems.apply {
            isNestedScrollingEnabled = false
            setControllerAndBuildModels(controller)
        }
    }

    fun setItemCounts(itemOrderedCounts: Map<Long, Int>?) {
        controller.orderedCountMap = itemOrderedCounts
    }

    fun getView() = mView

    override fun onItemAdded(item: MenuItemModel?): Boolean = item?.let {
        mListener?.onMenuItemAdded(it)
    } ?: false

    override fun onItemLongPress(item: MenuItemModel): Boolean = mListener?.run {
        onMenuItemShowInfo(item)
        true
    } ?: false

    override fun onItemChanged(item: MenuItemModel?, count: Int): Boolean = item?.let {
        mListener?.onMenuItemChanged(it, count)
    } ?: false
}
