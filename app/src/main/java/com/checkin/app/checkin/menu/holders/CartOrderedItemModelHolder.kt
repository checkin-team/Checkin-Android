package com.checkin.app.checkin.menu.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.DebouncedOnClickListener
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_cart_order_item)
abstract class CartOrderedItemModelHolder : EpoxyModelWithHolder<CartOrderedItemModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var orderedItem: OrderedItemModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: CartOrderInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(orderedItem)

    class Holder(val listener: CartOrderInteraction) : BaseEpoxyHolder<OrderedItemModel>() {
        @BindView(R.id.im_cart_ordered_item_type)
        internal lateinit var imItemType: ImageView
        @BindView(R.id.tv_cart_ordered_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_cart_ordered_item_customize)
        internal lateinit var tvCustomize: TextView
        @BindView(R.id.tv_cart_ordered_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.tv_menu_item_add_quantity)
        internal lateinit var tvItemQuantity: TextView
        @BindView(R.id.im_menu_item_quantity_increment)
        internal lateinit var imQuantityIncrement: ImageView
        @BindView(R.id.im_menu_item_quantity_decrement)
        internal lateinit var imQuantityDecrement: ImageView

        private lateinit var mData: OrderedItemModel

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            imQuantityIncrement.setOnClickListener(DebouncedOnClickListener { listener.onItemChange(mData, mData.quantity + 1) })
            imQuantityDecrement.setOnClickListener(DebouncedOnClickListener { listener.onItemChange(mData, mData.quantity - 1) })
        }

        override fun bindData(data: OrderedItemModel) {
            mData = data

            tvItemName.text = data.itemModel.name
            tvCustomize.visibility = if (data.isCustomized) View.VISIBLE else View.GONE
            tvItemQuantity.text = data.quantity.toString()
            tvItemPrice.text = Utils.formatCurrencyAmount(context, data.cost)
            imItemType.setImageResource(if (!data.itemModel.isVegetarian) R.drawable.ic_non_veg else R.drawable.ic_veg)
        }
    }
}

interface CartOrderInteraction {
    fun onItemChange(orderedItemModel: OrderedItemModel, newCount: Int)
}