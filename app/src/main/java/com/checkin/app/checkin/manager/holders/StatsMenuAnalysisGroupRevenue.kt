package com.checkin.app.checkin.manager.holders


import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.models.ManagerStatsModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import java.util.*

@EpoxyModelClass(layout = R.layout.item_menu_analysis_group_revenue)
abstract class StatsMenuAnalysisGroupRevenue : EpoxyModelWithHolder<StatsMenuAnalysisGroupRevenue.Holder>() {
    @EpoxyAttribute
    lateinit var data: ManagerStatsModel.GroupRevenueModel

    @EpoxyAttribute
    var index: Int = 0

    override fun bind(holder: Holder) = holder.bindData(index to data)

    class Holder : BaseEpoxyHolder<Pair<Int, ManagerStatsModel.GroupRevenueModel>>() {
        @BindView(R.id.tv_menu_analysis_group_index)
        internal lateinit var tvGroupIndex: TextView

        @BindView(R.id.tv_menu_analysis_group_name)
        internal lateinit var tvItemName: TextView

        @BindView(R.id.tv_menu_analysis_group_revenue)
        internal lateinit var tvRevenue: TextView

        override fun bindData(input: Pair<Int, ManagerStatsModel.GroupRevenueModel>) {
            val data = input.second
            tvGroupIndex.text = input.first.toString()
            tvItemName.text = data.group
            tvRevenue.text = String.format(Locale.getDefault(), "%.2f %% revenue contribution", data.revenueContribution)
        }
    }
}