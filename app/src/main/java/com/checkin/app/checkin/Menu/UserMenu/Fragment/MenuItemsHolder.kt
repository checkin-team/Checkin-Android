package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuItemAdapter
import com.checkin.app.checkin.R

class MenuItemsHolder(menuItems: List<MenuItemModel>, private var mListener: MenuItemInteraction?, container: ViewGroup) : MenuItemAdapter.OnItemInteractionListener {
    private val mView: View = LayoutInflater.from(container.context).inflate(R.layout.fragment_as_menu_items, container, false)

    @BindView(R.id.rv_menu_items)
    internal lateinit var rvMenuItems: RecyclerView

    private var mAdapter: MenuItemAdapter = MenuItemAdapter(menuItems, this, true)

    init {
        ButterKnife.bind(this, mView)
        rvMenuItems.isNestedScrollingEnabled = false
        rvMenuItems.adapter = mAdapter
        rvMenuItems.layoutManager = LinearLayoutManager(container.context, RecyclerView.VERTICAL, false)
    }

    fun getView() = mView

    override fun onItemAdded(item: MenuItemModel?): Boolean = mListener?.onMenuItemAdded(item)
            ?: false

    override fun onItemLongPress(item: MenuItemModel): Boolean = mListener?.run {
        onMenuItemShowInfo(item)
        true
    } ?: false

    override fun onItemChanged(item: MenuItemModel?, count: Int): Boolean = mListener?.onMenuItemChanged(item, count)
            ?: false

    override fun orderedItemCount(item: MenuItemModel): Int = mListener?.getItemOrderedCount(item)
            ?: 0
}
