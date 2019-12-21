package com.checkin.app.checkin.menu.holders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.session.models.TrendingDishModel

@EpoxyModelClass(layout = R.layout.item_menu_bestseller_dish)
abstract class BestSellerModelHolder : EpoxyModelWithHolder<BestSellerModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var trendingItem: TrendingDishModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: SessionTrendingDishInteraction

    @EpoxyAttribute
    var itemOrderedCount: Int = 0

    override fun bind(holder: Holder) {
        holder.count = itemOrderedCount
        holder.bindData(trendingItem)
    }

    inner class Holder : BaseEpoxyHolder<TrendingDishModel>() {
        @BindView(R.id.im_menu_bestseller_item_photo)
        internal lateinit var imDish: ImageView
        @BindView(R.id.tv_menu_bestseller_item_name)
        internal lateinit var tvName: TextView
        @BindView(R.id.tv_menu_bestseller_item_price)
        internal lateinit var tvPrice: TextView
        @BindView(R.id.container_menu_item_add_quantity)
        internal lateinit var containerAddQuantity: ViewGroup
        @BindView(R.id.tv_menu_item_add)
        internal lateinit var tvMenuItemAdd: TextView
        @BindView(R.id.tv_menu_item_add_quantity)
        internal lateinit var tvItemQuantity: TextView
        @BindView(R.id.im_menu_item_quantity_increment)
        internal lateinit var imQuantityIncrement: ImageView
        @BindView(R.id.im_menu_item_quantity_decrement)
        internal lateinit var imQuantityDecrement: ImageView

        private lateinit var mData: TrendingDishModel
        internal var count = 0

        override fun bindView(itemView: View) {
            super.bindView(itemView)

            itemView.setOnClickListener { if (count == 0) tvMenuItemAdd.performClick() }
            tvMenuItemAdd.setOnClickListener { listener.onDishChange(mData, count + 1) }
            imQuantityIncrement.setOnClickListener {
                listener.onDishChange(mData, count + 1)
            }
            imQuantityDecrement.setOnClickListener {
                if (trendingItem.isComplexItem) {
                    disallowDecreaseCount()
                    return@setOnClickListener
                }
                if (count <= 0) return@setOnClickListener
                listener.onDishChange(mData, count - 1)
            }
        }

        override fun bindData(data: TrendingDishModel) {
            mData = data

            Utils.loadImageOrDefault(imDish, data.image, 0)
            tvName.text = data.name
            tvPrice.text = Utils.formatCurrencyAmount(context, data.typeCosts[0])
            tvItemQuantity.text = count.toString()
            if (count > 0) showAddQuantity()
            else hideAddQuantity()

            if (data.isComplexItem) {
                tvMenuItemAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_setting, 0)
            } else {
                tvMenuItemAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        private fun disallowDecreaseCount() {
            Utils.toast(itemView.context, "Not allowed to change item count from here - use cart.")
        }

        private fun showAddQuantity() {
            containerAddQuantity.visibility = View.VISIBLE
            tvMenuItemAdd.visibility = View.GONE
        }

        private fun hideAddQuantity() {
            containerAddQuantity.visibility = View.GONE
            tvMenuItemAdd.visibility = View.VISIBLE
        }
    }
}

interface SessionTrendingDishInteraction {
    fun onDishChange(itemModel: TrendingDishModel, changeCount: Int)
}
