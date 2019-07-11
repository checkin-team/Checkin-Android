package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils

class MenuCartAdapter(private val mListener: MenuCartInteraction) : RecyclerView.Adapter<MenuCartAdapter.ViewHolder>() {
    private var orderedItems: List<OrderedItemModel>? = null

    override fun getItemCount(): Int = orderedItems?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ViewHolder(this) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindData(orderedItems!![position])

    override fun getItemViewType(position: Int): Int = R.layout.item_as_menu_cart

    fun setOrderedItems(orderedItems: List<OrderedItemModel>) {
        this.orderedItems = orderedItems
        Handler().post { notifyDataSetChanged() }
    }

    interface MenuCartInteraction {
        fun onOrderedItemRemark(item: OrderedItemModel, s: String)

        fun onOrderedItemChanged(item: OrderedItemModel?, count: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_menu_cart_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_menu_cart_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.tv_menu_cart_item_customized)
        internal lateinit var tvItemCustomized: TextView
        @BindView(R.id.tv_menu_item_quantity_decrement)
        internal lateinit var tvQuantityDecrement: TextView
        @BindView(R.id.tv_menu_item_quantity_number)
        internal lateinit var tvQuantityNumber: TextView
        @BindView(R.id.tv_menu_item_quantity_increment)
        internal lateinit var tvQuantityIncrement: TextView
        @BindView(R.id.et_as_menu_cart_special_instruction)
        internal lateinit var etSpecialInstructions: EditText

        private var mItem: OrderedItemModel? = null

        init {
            ButterKnife.bind(this, itemView)

            tvQuantityDecrement.setOnClickListener { mItem?.let { mListener.onOrderedItemChanged(it, it.quantity - 1) } }

            tvQuantityIncrement.setOnClickListener { mItem?.let { mListener.onOrderedItemChanged(it, it.quantity + 1) } }

            etSpecialInstructions.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    mListener.onOrderedItemRemark(mItem!!, s.toString())
                }
            })
        }

        fun bindData(item: OrderedItemModel) {
            try {
                mItem = item.clone()
            } catch (e: CloneNotSupportedException) {
            }

            tvItemName.text = item.itemModel.name
            tvItemPrice.text = Utils.formatIntegralCurrencyAmount(tvItemPrice.context, item.cost)
            if (item.isCustomized) tvItemCustomized.visibility = View.VISIBLE
            else tvItemCustomized.visibility = View.GONE

            if (item.itemModel.isVegetarian) tvItemName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0)
            else tvItemName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0)

            tvQuantityNumber.text = item.quantity.toString()
        }
    }
}
