package com.checkin.app.checkin.menu.holders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

@EpoxyModelClass(layout = R.layout.item_as_menu_cart)
abstract class ActiveSessionMenuCartModelHolder : EpoxyModelWithHolder<ActiveSessionMenuCartModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var orderData: OrderedItemModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: MenuCartInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(orderData)

    class Holder(val listener: MenuCartInteraction) : BaseEpoxyHolder<OrderedItemModel>() {
        @BindView(R.id.tv_menu_cart_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_menu_cart_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.tv_menu_cart_item_customized)
        internal lateinit var tvItemCustomized: TextView
        @BindView(R.id.et_as_menu_cart_special_instruction)
        internal lateinit var etSpecialInstructions: EditText
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

        private lateinit var mData: OrderedItemModel

        override fun bindView(itemView: View) {
            super.bindView(itemView)

            imQuantityIncrement.setOnClickListener(DebouncedOnClickListener { listener.onOrderedItemChanged(mData, mData.quantity + 1) })
            imQuantityDecrement.setOnClickListener(DebouncedOnClickListener { listener.onOrderedItemChanged(mData, mData.quantity - 1) })
            showAddQuantity()
            etSpecialInstructions.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    listener.onOrderedItemRemark(mData, s.toString())
                }
            })
        }

        override fun bindData(data: OrderedItemModel) {
            mData = data

            tvItemName.text = data.itemModel.name
            tvItemQuantity.text = data.quantity.toString()
            tvItemPrice.text = Utils.formatCurrencyAmount(context, data.cost)
            if (data.isCustomized()) tvItemCustomized.visibility = View.VISIBLE
            else tvItemCustomized.visibility = View.GONE

            if (data.itemModel.isVegetarian) tvItemName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0)
            else tvItemName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0)

        }

        private fun showAddQuantity() {
            containerAddQuantity.visibility = View.VISIBLE
            tvMenuItemAdd.visibility = View.GONE
        }
    }

    interface MenuCartInteraction {
        fun onOrderedItemRemark(item: OrderedItemModel, s: String)
        fun onOrderedItemChanged(item: OrderedItemModel, count: Int)
    }
}
