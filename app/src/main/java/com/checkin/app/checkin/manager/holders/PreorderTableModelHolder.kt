package com.checkin.app.checkin.manager.holders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.callPhoneNumber
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import java.util.*

@EpoxyModelClass(layout = R.layout.item_manager_preorder_new_table)
abstract class NewPreorderTableModelHolder : EpoxyModelWithHolder<NewPreorderTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: PreorderTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: PreorderTableInteraction) : BasePreorderTableHolder() {
        @BindView(R.id.tv_manager_preorder_due)
        internal lateinit var tvPreorderDue: TextView

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickNewTable(mData) }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
            tvPreorderDue.text = Utils.formatElapsedTime(data.created)
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_manager_preorder_preparation_table)
abstract class PreparationPreorderTableModelHolder : EpoxyModelWithHolder<PreparationPreorderTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: PreorderTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: PreorderTableInteraction) : BasePreorderTableHolder() {
        @BindView(R.id.tv_manager_preorder_due)
        internal lateinit var tvPreorderDue: TextView
        @BindView(R.id.tv_manager_preorder_call)
        internal lateinit var tvCallButton: TextView
        @BindView(R.id.tv_manager_preorder_guest_status)
        internal lateinit var tvGuestStatus: TextView
        @BindView(R.id.container_manager_preparation_table_food_served)
        internal lateinit var containerMarkFoodServed: ViewGroup

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickPreparationTable(mData) }
            tvCallButton.setOnClickListener {
                mData.owner.phoneNo?.callPhoneNumber(itemView.context)
            }
            containerMarkFoodServed.setOnClickListener {
                listener.onMarkFoodServed(mData)
            }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
            data.scheduled.let {
                val time = Calendar.getInstance().time
                if (it.plannedDatetime!! > time) {
                    tvPreorderDue.text = context.getString(R.string.format_due_time, Utils.formatDueTime(time, it.plannedDatetime))
                    tvPreorderDue.setTextColor(ContextCompat.getColor(context, R.color.brownish_grey))
                } else {
                    tvPreorderDue.text = context.getString(R.string.format_delay_time, Utils.formatDueTime(it.plannedDatetime, time))
                    tvPreorderDue.setTextColor(ContextCompat.getColor(context, R.color.primary_red))
                }
            }
            data.checkedIn.let {
                val time = Calendar.getInstance().time
                if (it != null) {
                    tvGuestStatus.setText(R.string.text_preorder_guest_status_reached)
                } else {
                    if (time <= data.scheduled.plannedDatetime) tvGuestStatus.setText(R.string.text_preorder_guest_status_reach_soon)
                    else tvGuestStatus.setText(R.string.text_preorder_guest_status_delay)
                }
            }
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_manager_preorder_upcoming_table)
abstract class UpcomingPreorderTableModelHolder : EpoxyModelWithHolder<UpcomingPreorderTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: PreorderTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: PreorderTableInteraction) : BasePreorderTableHolder() {
        @BindView(R.id.tv_manager_preorder_call)
        internal lateinit var tvCallButton: TextView

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickUpcomingTable(mData) }
            tvCallButton.setOnClickListener {
                mData.owner.phoneNo?.callPhoneNumber(itemView.context)
            }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
        }
    }
}

interface PreorderTableInteraction {
    fun onClickNewTable(data: ShopScheduledSessionModel)
    fun onClickPreparationTable(data: ShopScheduledSessionModel)
    fun onMarkFoodServed(data: ShopScheduledSessionModel)
    fun onClickUpcomingTable(data: ShopScheduledSessionModel)
}