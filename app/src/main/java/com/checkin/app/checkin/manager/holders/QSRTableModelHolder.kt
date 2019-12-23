package com.checkin.app.checkin.manager.holders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel

@EpoxyModelClass(layout = R.layout.item_manager_qsr_new_table)
abstract class NewQsrTableModelHolder : EpoxyModelWithHolder<NewQsrTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: QSRTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: QSRTableInteraction) : BaseQsrTableHolder() {
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickNewTable(mData) }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_manager_qsr_preparation_table)
abstract class PreparationQsrTableModelHolder : EpoxyModelWithHolder<PreparationQsrTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: QSRTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: QSRTableInteraction) : BaseQsrTableHolder() {
        @BindView(R.id.tv_manager_qsr_food_status)
        internal lateinit var tvFoodStatus: TextView
        @BindView(R.id.container_manager_preparation_table_food_ready)
        internal lateinit var containerMarkFoodReady: ViewGroup

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickPreparationTable(mData) }
            containerMarkFoodReady.setOnClickListener {
                listener.onMarkFoodReady(mData)
            }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
            tvFoodStatus.setText(R.string.text_qsr_mark_food_ready)
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_manager_qsr_end_table)
abstract class EndQsrTableModelHolder : EpoxyModelWithHolder<EndQsrTableModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var scheduledSessionModel: ShopScheduledSessionModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: QSRTableInteraction

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(scheduledSessionModel)

    class Holder(val listener: QSRTableInteraction) : BaseQsrTableHolder() {
        @BindView(R.id.tv_manager_qsr_food_status)
        internal lateinit var tvFoodStatus: TextView
        @BindView(R.id.container_manager_preparation_table_end)
        internal lateinit var containerMarkFoodDone: ViewGroup

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onClickEndTable(mData) }
            containerMarkFoodDone.setOnClickListener {
                listener.onMarkSessionEnd(mData)
            }
        }

        override fun bindData(data: ShopScheduledSessionModel) {
            super.bindData(data)
            Utils.loadImageOrDefault(imCustomer, data.owner.displayPic, R.drawable.fore_male_white)
            tvFoodStatus.setText(R.string.text_qsr_mark_session_end)
        }
    }
}

interface QSRTableInteraction {
    fun onClickNewTable(data: ShopScheduledSessionModel)
    fun onClickPreparationTable(data: ShopScheduledSessionModel)
    fun onMarkFoodReady(data: ShopScheduledSessionModel)
    fun onMarkSessionEnd(data: ShopScheduledSessionModel)
    fun onClickEndTable(data: ShopScheduledSessionModel)
}