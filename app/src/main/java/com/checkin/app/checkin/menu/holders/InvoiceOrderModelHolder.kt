package com.checkin.app.checkin.menu.holders

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.utility.Utils

@EpoxyModelClass(layout = R.layout.item_invoice_order_without_customizations)
abstract class InvoiceOrderModelHolder : EpoxyModelWithHolder<InvoiceOrderModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var orderData: SessionOrderedItemModel

    override fun bind(holder: Holder) = holder.bindData(orderData)

    class Holder : BaseEpoxyHolder<SessionOrderedItemModel>() {
        @BindView(R.id.tv_invoice_order_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_invoice_order_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.tv_invoice_order_customized)
        internal lateinit var tvCustomized: TextView

        override fun bindData(data: SessionOrderedItemModel) {
            tvItemName.text = "${data.item.name} x ${data.quantity} (${data.itemType})"
            tvItemPrice.text = Utils.formatCurrencyAmount(itemView.context, data.cost)
            tvCustomized.visibility = if (data.isCustomized) View.VISIBLE else View.GONE
            if (!data.item.isVegetarian) tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0)
            else tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0)
        }
    }
}