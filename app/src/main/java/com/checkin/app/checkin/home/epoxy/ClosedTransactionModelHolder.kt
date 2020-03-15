package com.checkin.app.checkin.home.epoxy

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.activities.ClosedSessionDetailsActivity
import com.checkin.app.checkin.home.model.ClosedSessionBriefModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_transaction_details)
abstract class ClosedTransactionModelHolder : EpoxyModelWithHolder<ClosedTransactionModelHolder.Holder>() {

    @EpoxyAttribute
    internal lateinit var data: ClosedSessionBriefModel


    override fun bind(holder: Holder) {
        holder.bindData(data)
    }

    class Holder : BaseEpoxyHolder<ClosedSessionBriefModel>() {
        @BindView(R.id.tv_transaction_restaurant)
        internal lateinit var tvTransactionRestaurant: TextView

        @BindView(R.id.tv_transaction_id)
        internal lateinit var tvTransactionId: TextView

        @BindView(R.id.tv_transaction_timings)
        internal lateinit var tvTransactionTimings: TextView

        @BindView(R.id.tv_transaction_count)
        internal lateinit var tvTransactionCount: TextView

        @BindView(R.id.tv_transaction_amount)
        internal lateinit var tvTransactionAmount: TextView


        override fun bindData(data: ClosedSessionBriefModel) {
            tvTransactionRestaurant.text = data.restaurant.displayName
            tvTransactionId.text = data.formatId()
            tvTransactionTimings.text = data.formatTimings()
            tvTransactionCount.text = data.countCustomers.toString()
            tvTransactionAmount.text = data.formatAmount()

            itemView.setOnClickListener { _ ->
                val intent = ClosedSessionDetailsActivity.withSessionIntent(context, data.pk)
                context.startActivity(intent)
            }


        }

    }
}