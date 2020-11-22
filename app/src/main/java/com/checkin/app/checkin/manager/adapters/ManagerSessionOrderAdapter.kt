package com.checkin.app.checkin.manager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.models.ItemCustomizationGroupModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.utility.HeaderFooterRecyclerViewAdapter
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.formatPriceWithOff

class ManagerSessionOrderAdapter(private val mListener: SessionOrdersInteraction, private val mShowFooter: Boolean) : HeaderFooterRecyclerViewAdapter() {
    private var mOrders: List<SessionOrderedItemModel>? = null

    fun setData(data: List<SessionOrderedItemModel>?) {
        mOrders = data
        notifyDataSetChanged()
    }

    override fun useHeader(): Boolean = false

    override fun useFooter(): Boolean = mShowFooter

    override fun onCreateFooterViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manager_session_confirm_order, parent, false)
        return FooterViewHolder(view)
    }

    override fun onCreateBasicItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindBasicItemView(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindData(mOrders!![position])
    }

    override fun getBasicItemCount(): Int = mOrders?.size ?: 0

    override fun getBasicItemType(position: Int): Int = R.layout.item_manager_session_order

    interface SessionOrdersInteraction {
        fun confirmNewOrders()
        fun onSelectDeselect(orderedItem: SessionOrderedItemModel?, statusType: CHAT_STATUS_TYPE?)
        fun onOrderStatusChange(orderedItem: SessionOrderedItemModel?, statusType: CHAT_STATUS_TYPE?)
    }

    internal inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.findViewById<View>(R.id.btn_ms_order_confirm).setOnClickListener { v: View? -> mListener.confirmNewOrders() }
        }
    }

    inner class ViewHolder internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.tv_ms_order_item_name)
        internal lateinit var tvItemName: TextView

        @BindView(R.id.tv_ms_order_item_quantity)
        internal lateinit var tvQuantity: TextView

        @BindView(R.id.cb_ms_order_accept)
        internal lateinit var cbOrderAccept: CheckBox

        @BindView(R.id.tv_ms_price)
        internal lateinit var tvPrice: TextView

        @BindView(R.id.btn_ms_order_done)
        internal lateinit var btnOrderDone: Button

        @BindView(R.id.tv_ms_order_remarks)
        internal lateinit var tvRemarks: TextView

        @BindView(R.id.container_ms_order_customizations)
        internal lateinit var containerCustomizations: ViewGroup

        @BindView(R.id.container_ms_order_customizations_left)
        internal lateinit var containerCustomizationsLeft: LinearLayout

        @BindView(R.id.container_ms_order_customizations_right)
        internal lateinit var containerCustomizationsRight: LinearLayout

        @BindView(R.id.container_ms_order_remarks)
        internal lateinit var containerRemarks: LinearLayout

        @BindView(R.id.tv_ms_order_status)
        internal lateinit var tvOrderStatus: TextView

        @BindView(R.id.container_ms_order_status_open)
        internal lateinit var containerStatusOpen: ViewGroup

        private var mOrderModel: SessionOrderedItemModel? = null

        private fun resetLayout() {
            containerRemarks.visibility = View.GONE
            containerCustomizations.visibility = View.GONE
            containerStatusOpen.visibility = View.GONE
            tvOrderStatus.visibility = View.GONE
            btnOrderDone.visibility = View.GONE
        }

        fun bindData(order: SessionOrderedItemModel) {
            resetLayout()
            mOrderModel = order
            tvItemName.text = order.item.name
            tvQuantity.text = order.formatQuantityItemType()
            val isVeg = order.item.isVegetarian
            if (isVeg != null) {
                tvItemName.setCompoundDrawablesWithIntrinsicBounds(if (isVeg) R.drawable.ic_veg else R.drawable.ic_non_veg, 0, 0, 0)
            } else tvItemName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            if (order.remarks != null) {
                tvRemarks.text = order.remarks
                containerRemarks.visibility = View.VISIBLE
            }
            if (order.customizations.size > 0) {
                containerCustomizations.visibility = View.VISIBLE
                containerCustomizationsRight.removeAllViews()
                containerCustomizationsLeft.removeAllViews()
                for (i in order.customizations.indices) {
                    addCustomizations(
                            itemView.context, order.customizations[i], if (i % 2 == 0) containerCustomizationsLeft else containerCustomizationsRight)
                }
            }
            if (order.status == CHAT_STATUS_TYPE.OPEN) {
                containerStatusOpen.visibility = View.VISIBLE
                tvPrice.text = order.formatPriceWithOff(itemView.context)
            } else {
                when (order.status) {
                    CHAT_STATUS_TYPE.IN_PROGRESS -> btnOrderDone.visibility = View.VISIBLE
                    CHAT_STATUS_TYPE.COOKED -> {
                        btnOrderDone.visibility = View.VISIBLE
                        tvOrderStatus.setText(R.string.label_cooked)
                        tvOrderStatus.visibility = View.VISIBLE
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.context.resources.getColor(R.color.apple_green))
                    }
                    CHAT_STATUS_TYPE.DONE -> {
                        tvOrderStatus.setText(R.string.status_order_delivered)
                        tvOrderStatus.visibility = View.VISIBLE
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.context.resources.getColor(R.color.apple_green))
                    }
                    CHAT_STATUS_TYPE.CANCELLED -> {
                        tvOrderStatus.setText(R.string.status_cancelled)
                        tvOrderStatus.visibility = View.VISIBLE
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.context.resources.getColor(R.color.primary_red))
                    }
                }
            }
        }

        fun addCustomizations(context: Context?, group: ItemCustomizationGroupModel, container: ViewGroup?) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_active_session_order_customization, container, false)
            val tvGroupName = view.findViewById<TextView>(R.id.tv_order_customization_group)
            val tvFieldNames = view.findViewById<TextView>(R.id.tv_order_customization_names)
            tvGroupName.text = group.name
            tvFieldNames.text = Utils.joinCollection(group.customizationFields, "\n")
            container!!.addView(view)
        }

        init {
            ButterKnife.bind(this, itemView!!)
            btnOrderDone.setOnClickListener { _ -> mListener.onOrderStatusChange(mOrderModel, CHAT_STATUS_TYPE.DONE) }
            cbOrderAccept.setOnCheckedChangeListener { _, isChecked -> if (isChecked) mListener.onSelectDeselect(mOrderModel, CHAT_STATUS_TYPE.IN_PROGRESS) else mListener.onSelectDeselect(mOrderModel, CHAT_STATUS_TYPE.CANCELLED) }
        }
    }
}