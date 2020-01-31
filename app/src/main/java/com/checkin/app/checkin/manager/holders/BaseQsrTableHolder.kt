package com.checkin.app.checkin.manager.holders

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.utility.Utils
import java.util.*

abstract class BaseQsrTableHolder : BaseEpoxyHolder<ShopScheduledSessionModel>() {
    @BindView(R.id.tv_manager_qsr_customer_name)
    internal lateinit var tvCustomerName: TextView
    @BindView(R.id.tv_manager_qsr_scheduled_info)
    internal lateinit var tvScheduledInfo: TextView
    @BindView(R.id.im_manager_qsr_customer)
    internal lateinit var imCustomer: ImageView
    @BindView(R.id.tv_manager_qsr_amount)
    internal lateinit var tvAmount: TextView

    internal lateinit var mData: ShopScheduledSessionModel

    @CallSuper
    override fun bindData(data: ShopScheduledSessionModel) {
        mData = data

        tvCustomerName.text = data.owner.displayName!!
        tvAmount.text = Utils.formatCurrencyAmount(context, data.amount)

        tvScheduledInfo.text = when (data.scheduled.status) {
            ScheduledSessionStatus.PENDING -> "Order Time: ${data.scheduled.formatOrderTime}"
            else -> {
                val currTime = Calendar.getInstance().time
                if (data.scheduled.plannedDatetime!! < currTime) {
                    tvScheduledInfo.setTextColor(ContextCompat.getColor(context, R.color.primary_red))
                    tvScheduledInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_error_exclamation, 0, 0, 0)
                    tvScheduledInfo.compoundDrawablePadding = itemView.resources.getDimension(R.dimen.spacing_tiny).toInt()
                    "Delayed by: ${Utils.formatDueTime(data.scheduled.plannedDatetime, currTime)}"
                } else {
                    tvScheduledInfo.setTextColor(ContextCompat.getColor(context, R.color.brownish_grey))
                    tvScheduledInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    "Due Time: ${Utils.formatDueTime(currTime, data.scheduled.plannedDatetime)}"
                }
            }
        }
    }
}