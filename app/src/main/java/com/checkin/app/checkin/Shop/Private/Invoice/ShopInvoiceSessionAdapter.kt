package com.checkin.app.checkin.Shop.Private.Invoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceSessionAdapter.ShopInvoiceHolder
import com.checkin.app.checkin.Shop.ShopModel
import com.checkin.app.checkin.utility.Utils
import java.util.*

class ShopInvoiceSessionAdapter(private val mListener: ShopInvoiceInteraction) : RecyclerView.Adapter<ShopInvoiceHolder>() {
    private var mData: List<RestaurantSessionModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInvoiceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_invoice_session, parent, false)
        return ShopInvoiceHolder(view)
    }

    override fun onBindViewHolder(holder: ShopInvoiceHolder, position: Int) = holder.bindData(mData[position])

    override fun getItemCount(): Int = mData.size

    fun setSessionData(data: List<RestaurantSessionModel>) {
        mData = data
        notifyDataSetChanged()
    }

    interface ShopInvoiceInteraction {
        fun onClickSession(data: RestaurantSessionModel?)
    }

    inner class ShopInvoiceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_invoice_session_id)
        internal lateinit var tvSessionId: TextView
        @BindView(R.id.tv_invoice_session_date)
        internal lateinit var tvDate: TextView
        @BindView(R.id.tv_invoice_session_bill)
        internal lateinit var tvBill: TextView
        @BindView(R.id.tv_invoice_session_item_count)
        internal lateinit var tvItemCount: TextView
        @BindView(R.id.tv_invoice_session_paid_via)
        internal lateinit var tvPaidVia: TextView
        @BindView(R.id.tv_invoice_session_waiter)
        internal lateinit var tvWaiter: TextView
        @BindView(R.id.tv_invoice_session_table)
        internal lateinit var tvTable: TextView

        private var mData: RestaurantSessionModel? = null

        fun bindData(data: RestaurantSessionModel) {
            mData = data
            if (data.isScheduled) {
                tvWaiter.visibility = View.GONE
            } else {
                tvWaiter.visibility = View.VISIBLE
                tvWaiter.text = String.format(Locale.ENGLISH, "Waiter : %s", data.host?.displayName
                        ?: itemView.resources.getString(R.string.waiter_unassigned))
            }
            tvSessionId.text = data.formatHashId
            tvBill.text = String.format(Locale.ENGLISH, Utils.getCurrencyFormat(itemView.context), data.formatTotal())
            tvDate.text = data.formattedDate
            tvItemCount.text = String.format(Locale.ENGLISH, " | %d item(s)", data.countOrders)
            tvTable.text = data.tableInfo
            if (data.paymentMode != null)
                tvPaidVia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, ShopModel.getPaymentModeIcon(data.paymentMode))
        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener { v: View? -> mListener.onClickSession(mData) }
        }
    }

}