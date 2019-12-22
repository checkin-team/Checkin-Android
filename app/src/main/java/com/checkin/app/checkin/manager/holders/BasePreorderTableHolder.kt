package com.checkin.app.checkin.manager.holders

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

abstract class BasePreorderTableHolder : BaseEpoxyHolder<ShopScheduledSessionModel>() {
    @BindView(R.id.tv_manager_preorder_customer_name)
    internal lateinit var tvCustomerName: TextView
    @BindView(R.id.tv_manager_preorder_scheduled_info)
    internal lateinit var tvScheduledInfo: TextView
    @BindView(R.id.im_manager_preorder_customer)
    internal lateinit var imCustomer: ImageView

    internal lateinit var mData: ShopScheduledSessionModel

    @CallSuper
    override fun bindData(data: ShopScheduledSessionModel) {
        mData = data

        tvCustomerName.text = data.owner.displayName!!
        tvScheduledInfo.text = "${data.scheduled.formatPlannedDate} | ${data.scheduled.formatPlannedTime} | ${data.scheduled.formatGuestCount}"
    }
}