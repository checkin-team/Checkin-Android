package com.checkin.app.checkin.Home.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Home.LiveSessionTrackerInteraction
import com.checkin.app.checkin.Home.LiveSessionViewHolder
import com.checkin.app.checkin.Home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.Home.model.LiveSessionDetailModel
import com.checkin.app.checkin.Home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.callPhoneNumber
import com.checkin.app.checkin.Utility.navigateToLocation

class ActiveLiveSessionViewHolder(itemView: View, interactionListener: LiveSessionTrackerInteraction) : LiveSessionViewHolder<ActiveLiveSessionDetailModel>(itemView, interactionListener) {
    @BindView(R.id.container_home_session_live_active_call_waiter)
    internal lateinit var containerCallWaiter: ViewGroup
    @BindView(R.id.container_home_session_live_active_menu)
    internal lateinit var containerMenu: ViewGroup
    @BindView(R.id.tv_home_session_live_active_message)
    internal lateinit var tvMessage: TextView
    @BindView(R.id.im_home_session_live_active_logo)
    internal lateinit var imRestaurantLogo: ImageView
    @BindView(R.id.tv_home_session_live_active_status)
    internal lateinit var tvStatus: TextView
    @BindView(R.id.tv_home_session_live_active_name)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_home_session_live_order_new_count)
    internal lateinit var tvNewCount: TextView
    @BindView(R.id.tv_home_session_live_order_progress_count)
    internal lateinit var tvProgressCount: TextView
    @BindView(R.id.tv_home_session_live_order_done_count)
    internal lateinit var tvDoneCount: TextView
    @BindView(R.id.tv_home_session_live_active_promo_name)
    internal lateinit var tvPromoName: TextView

    private var mData: ActiveLiveSessionDetailModel? = null

    init {
        ButterKnife.bind(this, itemView)

        containerCallWaiter.setOnClickListener {
            mData?.let {
                Utils.toast(itemView.context, "TODO: call waiter")
            }
        }

        containerMenu.setOnClickListener {
            mData?.let {
                interactionListener.onOpenRestaurantMenu(it)
            }
        }
    }

    override fun getData(): LiveSessionDetailModel? = mData

    override fun bindData(data: ActiveLiveSessionDetailModel) {
        mData = data

        tvMessage.text = itemView.context.getString(R.string.format_live_session_active_message).format(username)
        tvRestaurantName.text = data.restaurant.name
        Utils.loadImageOrDefault(imRestaurantLogo, data.restaurant.logo, R.drawable.cover_restaurant_unknown)
        data.offers.getOrNull(0)?.let {
            tvPromoName.text = Utils.fromHtml(it.name)
        }
        tvNewCount.text = data.newOrdersCount.toString()
        tvDoneCount.text = data.doneOrdersCount.toString()
        tvProgressCount.text = data.progressOrdersCount.toString()
    }
}

class QsrLiveSessionViewHolder(itemView: View, interactionListener: LiveSessionTrackerInteraction) : LiveSessionViewHolder<ScheduledLiveSessionDetailModel>(itemView, interactionListener) {
    @BindView(R.id.im_home_session_live_qsr_logo)
    internal lateinit var imRestaurantLogo: ImageView
    @BindView(R.id.container_home_session_live_qsr_order_more)
    internal lateinit var containerOrderMore: ViewGroup
    @BindView(R.id.im_home_session_live_qsr_order_status)
    internal lateinit var imOrderStatus: ImageView
    @BindView(R.id.tv_home_session_live_qsr_order_status)
    internal lateinit var tvOrderStatus: TextView
    @BindView(R.id.tv_home_session_live_qsr_message)
    internal lateinit var tvMessage: TextView
    @BindView(R.id.tv_home_session_live_qsr_name)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_home_session_live_qsr_orders_summary)
    internal lateinit var tvOrderSummary: TextView
    @BindView(R.id.tv_item_session_live_qsr_amount)
    internal lateinit var tvAmount: TextView
    @BindView(R.id.tv_item_session_live_qsr_session_id)
    internal lateinit var tvSessionId: TextView
    @BindView(R.id.tv_home_session_live_qsr_status)
    internal lateinit var tvSessionStatus: TextView

    private var mData: ScheduledLiveSessionDetailModel? = null

    init {
        ButterKnife.bind(this, itemView)

        containerOrderMore.setOnClickListener {
            mData?.let {
                interactionListener.onOpenRestaurantMenu(it)
            }
        }
    }

    override fun getData(): LiveSessionDetailModel? = mData

    override fun bindData(data: ScheduledLiveSessionDetailModel) {
        mData = data

        tvAmount.text = Utils.formatCurrencyAmount(itemView.context, data.amount)
        tvSessionStatus.text = data.scheduled.status.repr
        tvMessage.text = itemView.context.getString(R.string.format_live_session_qsr_message).format(username)
        tvRestaurantName.text = data.restaurant.name
        Utils.loadImageOrDefault(imRestaurantLogo, data.restaurant.logo, R.drawable.cover_restaurant_unknown)
        tvSessionId.text = data.formatSessionId
        if (data.isOrderInProgress) {
            imOrderStatus.setImageResource(R.drawable.ic_order_status_cooking)
            tvOrderStatus.text = itemView.context.getString(R.string.msg_order_status_in_progress)
        } else {
            imOrderStatus.setImageResource(R.drawable.ic_order_status_cooking)
            tvOrderStatus.text = itemView.context.getString(R.string.msg_order_status_being_served)
        }
        tvOrderSummary.text = when {
            data.orderedItems.isNullOrEmpty() -> "-"
            data.orderedItems.size > 1 -> "${data.orderedItems[0].name} (${data.orderedItems[0].quantity})... ${data.orderedItems.size - 1} more"
            else -> "${data.orderedItems[0].name} (${data.orderedItems[0].quantity})"
        }
    }
}

class PreDiningLiveSessionViewHolder(itemView: View, interactionListener: LiveSessionTrackerInteraction) : LiveSessionViewHolder<ScheduledLiveSessionDetailModel>(itemView, interactionListener) {
    @BindView(R.id.im_home_session_live_predining_logo)
    internal lateinit var imRestaurantLogo: ImageView
    @BindView(R.id.im_item_session_live_predining_share)
    internal lateinit var imShare: ImageView
    @BindView(R.id.tv_home_session_live_predining_message)
    internal lateinit var tvMessage: TextView
    @BindView(R.id.tv_home_session_live_predining_restaurant_name)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_home_session_live_predining_status)
    internal lateinit var tvSessionStatus: TextView
    @BindView(R.id.tv_item_session_live_predining_amount)
    internal lateinit var tvAmount: TextView
    @BindView(R.id.tv_item_session_live_predining_call)
    internal lateinit var tvCallButton: TextView
    @BindView(R.id.tv_item_session_live_predining_navigate)
    internal lateinit var tvNavigateButton: TextView
    @BindView(R.id.tv_item_session_live_predining_diners)
    internal lateinit var tvDiners: TextView
    @BindView(R.id.tv_item_session_live_predining_orders)
    internal lateinit var tvOrders: TextView
    @BindView(R.id.tv_item_session_live_scheduled_date)
    internal lateinit var tvScheduledDate: TextView
    @BindView(R.id.tv_item_session_live_scheduled_time)
    internal lateinit var tvScheduledTime: TextView
    @BindView(R.id.tv_item_session_live_predining_session_id)
    internal lateinit var tvSessionId: TextView

    private var mData: ScheduledLiveSessionDetailModel? = null

    init {
        ButterKnife.bind(this, itemView)

        tvNavigateButton.setOnClickListener {
            mData?.restaurant?.location?.navigateToLocation(itemView.context)
        }

        tvCallButton.setOnClickListener {
            mData?.restaurant?.phone?.callPhoneNumber(itemView.context)
        }

        imShare.setOnClickListener { Utils.toast(itemView.context, "TODO: Share!") }
    }

    override fun getData(): LiveSessionDetailModel? = mData

    override fun bindData(data: ScheduledLiveSessionDetailModel) {
        mData = data

        tvOrders.text = data.countOrders.toString()
        tvDiners.text = data.scheduled.countPeople.toString()
        tvAmount.text = Utils.formatCurrencyAmount(itemView.context, data.amount)
        tvSessionStatus.text = data.scheduled.status.repr
        tvMessage.text = itemView.context.getString(R.string.format_live_session_predining_message).format(username)
        tvRestaurantName.text = if (data.restaurant.location != null) data.restaurant.name + " - " + data.restaurant.location.locality else data.restaurant.name
        Utils.loadImageOrDefault(imRestaurantLogo, data.restaurant.logo, R.drawable.cover_restaurant_unknown)
        tvScheduledDate.text = data.scheduled.formatDate
        tvScheduledTime.text = data.scheduled.formatTime
        tvSessionId.text = data.formatSessionId
    }
}
