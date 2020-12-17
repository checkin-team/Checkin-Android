package com.checkin.app.checkin.manager.holders



import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.models.SimpleModelHeading
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_shop_manager_stats_heading)
abstract class ManagerStatsModelHeadingHolder : EpoxyModelWithHolder<ManagerStatsModelHeadingHolder.Holder>() {
    @EpoxyAttribute
    internal lateinit var data: SimpleModelHeading

    override fun bind(holder: Holder) = holder.bindData(data)

    class Holder : BaseEpoxyHolder<SimpleModelHeading>() {

        @BindView(R.id.title_manager_stats_heading)
        internal lateinit var tvHeading: TextView

        override fun bindData(data:SimpleModelHeading) {

            tvHeading.text = data.type

        }
    }
}
