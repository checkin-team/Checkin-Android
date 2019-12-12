package com.checkin.app.checkin.session.activesession

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.model.SessionOrderedItemModel

class InvoiceOrdersAdapter(private var mOrderedItems: List<SessionOrderedItemModel>?, private val mClickListener: OrderedItemClick?) : RecyclerView.Adapter<InvoiceOrdersAdapter.ViewHolder>() {

    fun setData(data: List<SessionOrderedItemModel>) {
        this.mOrderedItems = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ViewHolder(this) }

    override fun getItemViewType(position: Int) = R.layout.item_invoice_ordered_item

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mOrderedItems!![position])
    }

    override fun getItemCount() = mOrderedItems?.size ?: 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.tv_ordered_item_name)
        lateinit var tvName: TextView
        @BindView(R.id.tv_ordered_item_price)
        lateinit var tvPrice: TextView
        @BindView(R.id.im_ms_ordered_item_cancel)
        lateinit var imCancelOrder: ImageView
        private var mOrderedItem: SessionOrderedItemModel? = null

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener { mClickListener?.cancelOrderedItem(mOrderedItem) }
        }

        fun bindData(orderedItem: SessionOrderedItemModel) {
            mOrderedItem = orderedItem
            if (mClickListener != null)
                imCancelOrder.visibility = View.VISIBLE
            tvName.text = orderedItem.formatItemDetail()
            if (!orderedItem.item.isVegetarian)
                tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0)
            tvPrice.text = Utils.getCurrencyFormat(itemView.context).format(orderedItem.formatCost())
        }
    }

    interface OrderedItemClick {
        fun cancelOrderedItem(orderedItem: SessionOrderedItemModel?)
    }
}
