package com.checkin.app.checkin.menu.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.models.ItemCustomizationGroupModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.formatPriceWithOff

@EpoxyModelClass(layout = R.layout.item_invoice_order_with_customizations)
abstract class InvoiceOrderWithCustomizationModelHolder : EpoxyModelWithHolder<InvoiceOrderWithCustomizationModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var orderData: SessionOrderedItemModel

    override fun bind(holder: Holder) = holder.bindData(orderData)

    class Holder : BaseEpoxyHolder<SessionOrderedItemModel>() {
        @BindView(R.id.tv_invoice_order_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_invoice_order_item_price)
        internal lateinit var tvItemPrice: TextView
        @BindView(R.id.container_invoice_order_customizations)
        internal lateinit var customizationsContainer: ViewGroup
        @BindView(R.id.container_invoice_order_customizations_left)
        internal lateinit var customizationContainerLeft: ViewGroup
        @BindView(R.id.container_invoice_order_customizations_right)
        internal lateinit var customizationContainerRight: ViewGroup
        @BindView(R.id.container_invoice_order_remarks)
        internal lateinit var containerRemarks: ViewGroup
        @BindView(R.id.tv_invoice_order_remarks)
        internal lateinit var tvRemarks: TextView

        override fun bindData(data: SessionOrderedItemModel) {
            tvItemName.text = "${data.item.name} x ${data.quantity} (${data.itemType})"
            tvItemPrice.text = data.formatPriceWithOff(context)
            data.item.isVegetarian.let {
                if (it != null)
                    tvItemName.setCompoundDrawablesWithIntrinsicBounds(if (it) R.drawable.ic_veg else R.drawable.ic_non_veg, 0, 0, 0)
                else tvItemName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            data.remarks?.let {
                tvRemarks.text = it
                containerRemarks.visibility = View.VISIBLE
            } ?: run { containerRemarks.visibility = View.GONE }

            if (data.isCustomized) {
                customizationsContainer.visibility = View.VISIBLE
                customizationContainerLeft.removeAllViews()
                customizationContainerRight.removeAllViews()
                data.customizations.forEachIndexed { index, customizationGroupModel ->
                    addCustomizations(context, customizationGroupModel, if (index % 2 == 0) customizationContainerLeft else customizationContainerRight)
                }
            } else customizationsContainer.visibility = View.GONE
        }

        private fun addCustomizations(context: Context?, group: ItemCustomizationGroupModel, container: ViewGroup) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_active_session_order_customization, container, false)
            val tvGroupName = view.findViewById<TextView>(R.id.tv_order_customization_group)
            val tvFieldNames = view.findViewById<TextView>(R.id.tv_order_customization_names)
            tvGroupName.text = group.name
            tvFieldNames.text = Utils.joinCollection(group.customizationFields, "\n")
            container.addView(view)
        }
    }
}