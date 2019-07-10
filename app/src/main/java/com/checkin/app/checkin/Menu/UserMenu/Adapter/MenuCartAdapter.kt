package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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
        @BindView(R.id.im_menu_cart_item_type)
        internal lateinit var imType: ImageView
        @BindView(R.id.tv_menu_item_quantity_decrement)
        internal lateinit var tvQuantityDecrement: TextView
        @BindView(R.id.tv_menu_item_quantity_number)
        internal lateinit var tvQuantityNumber: TextView
        @BindView(R.id.tv_menu_item_quantity_increment)
        internal lateinit var tvQuantityIncrement: TextView
        @BindView(R.id.et_as_menu_cart_special_instruction)
        internal lateinit var etSpecialInstructions: EditText

        private var mItem: OrderedItemModel? = null
        private var mCount: Int = 0

        init {
            ButterKnife.bind(this, itemView)

            tvQuantityDecrement.setOnClickListener { decreaseQuantity() }

            tvQuantityIncrement.setOnClickListener { increaseQuantity() }

            tvQuantityNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    mCount = Integer.parseInt(s.toString())
                    mListener.onOrderedItemChanged(mItem, Integer.parseInt(s.toString()))
                }
            })

            etSpecialInstructions.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    mListener.onOrderedItemRemark(mItem!!, s.toString())
                }
            })
        }

        fun bindData(item: OrderedItemModel) {
            mItem = item;
//            try {
//                mItem = item.clone()
//            } catch (e: CloneNotSupportedException) {
//                e.printStackTrace()
//            }

            tvItemName.text = item.itemModel.name
            tvItemPrice.text = Utils.formatCurrencyAmount(tvItemPrice.context, item.cost)
            if (item.isCustomized) tvItemCustomized.visibility = View.VISIBLE
            else tvItemCustomized.visibility = View.GONE

            if (item.itemModel.isVegetarian) imType.setImageDrawable(imType.context.resources.getDrawable(R.drawable.ic_veg))
            else imType.setImageDrawable(imType.context.resources.getDrawable(R.drawable.ic_non_veg))

            displayQuantity(item.quantity)
            mCount = item.quantity
        }

        fun increaseQuantity() {
            mCount++
            displayQuantity(mCount)

        }

        fun decreaseQuantity() {
            if (mCount <= 0)
                return
            mCount--
            displayQuantity(mCount)
        }

        private fun displayQuantity(number: Int) {
            tvQuantityNumber.text = number.toString()
        }
    }
}
