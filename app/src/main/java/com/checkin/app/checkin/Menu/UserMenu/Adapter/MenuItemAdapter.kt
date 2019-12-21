package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R

class MenuItemAdapter(private var mItemsList: List<MenuItemModel>?, private val mListener: OnItemInteractionListener, private val mIsSessionActive: Boolean) : RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder>() {

    fun setMenuItems(menuItems: List<MenuItemModel>?) {
        this.mItemsList = menuItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = R.layout.item_as_menu_group_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ItemViewHolder(this) }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bindData(mItemsList!![position])

    override fun getItemCount() = mItemsList?.size ?: 0

    interface OnItemInteractionListener {
        fun onItemAdded(item: MenuItemModel?): Boolean

        fun onItemLongPress(item: MenuItemModel): Boolean

        fun onItemChanged(item: MenuItemModel?, count: Int): Boolean

        fun orderedItemCount(item: MenuItemModel): Int
    }

    inner class ItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal fun bindData(menuItem: MenuItemModel) {
        }
    }

    companion object {
        private val TAG = MenuItemAdapter::class.java.simpleName

        private val DEFAULT_STATE = 0
        private val DESCRIPTION_STATE = 1
        private val IMAGE_STATE = 2
    }
}
