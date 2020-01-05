package com.checkin.app.checkin.menu.holders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.DebouncedOnClickListener
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.misc.views.SwipeRevealLayout
import java.util.*

@EpoxyModelClass(layout = R.layout.item_menu_cart)
abstract class ShopMenuCartModelHolder : EpoxyModelWithHolder<ShopMenuCartModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var orderData: OrderedItemModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: MenuCartInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(orderData)

    class Holder(val listener: MenuCartInteraction) : BaseEpoxyHolder<OrderedItemModel>() {
        @BindView(R.id.btn_menu_cart_item_edit)
        internal lateinit var btnItemEdit: ImageButton
        @BindView(R.id.btn_menu_cart_item_remove)
        internal lateinit var btnItemRemove: ImageButton
        @BindView(R.id.tv_menu_cart_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_menu_cart_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.tv_menu_cart_item_extra)
        internal lateinit var tvItemExtra: TextView
        @BindView(R.id.tv_menu_item_add)
        internal lateinit var tvMenuItemAdd: TextView
        @BindView(R.id.container_menu_item_add_quantity)
        internal lateinit var containerAddQuantity: ViewGroup
        @BindView(R.id.tv_menu_item_add_quantity)
        internal lateinit var tvItemQuantity: TextView
        @BindView(R.id.im_menu_item_quantity_increment)
        internal lateinit var imQuantityIncrement: ImageView
        @BindView(R.id.im_menu_item_quantity_decrement)
        internal lateinit var imQuantityDecrement: ImageView
        @BindView(R.id.sr_menu_cart_item)
        internal lateinit var srCartItem: SwipeRevealLayout
        @BindView(R.id.container_menu_cart_item)
        internal lateinit var containerMenuCartItem: ViewGroup

        private lateinit var mData: OrderedItemModel

        override fun bindView(itemView: View) {
            super.bindView(itemView)

            containerMenuCartItem.setOnClickListener { if (srCartItem.isOpened) srCartItem.close(true) else srCartItem.open(true) }
            btnItemRemove.setOnClickListener { listener.onOrderedItemRemoved(mData) }
            btnItemEdit.setOnClickListener { listener.onOrderedItemRemark(mData) }
            imQuantityIncrement.setOnClickListener(DebouncedOnClickListener { listener.onOrderedItemChanged(mData, mData.quantity + 1) })
            imQuantityDecrement.setOnClickListener(DebouncedOnClickListener { listener.onOrderedItemChanged(mData, mData.quantity - 1) })
            showAddQuantity()
        }

        override fun bindData(data: OrderedItemModel) {
            mData = data

            tvItemName.text = data.itemModel.name
            tvItemExtra.text = String.format(
                    Locale.ENGLISH, "%d %s %s", data.quantity, if (data.typeName() != null) data.typeName() else "", if (data.isCustomized()) "(Customized)" else "")
            tvItemQuantity.text = data.quantity.toString()
            tvItemPrice.text = Utils.formatCurrencyAmount(context, data.cost)
        }

        private fun showAddQuantity() {
            containerAddQuantity.visibility = View.VISIBLE
            tvMenuItemAdd.visibility = View.GONE
        }
    }
}

interface MenuCartInteraction {
    fun onOrderedItemRemark(item: OrderedItemModel)
    fun onOrderedItemRemoved(item: OrderedItemModel)
    fun onOrderedItemChanged(item: OrderedItemModel, count: Int)
}
