package com.checkin.app.checkin.manager.holders

import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.models.ManagerStatsModel
import com.checkin.app.checkin.menu.models.MenuItemBriefModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import java.util.*

@EpoxyModelClass(layout = R.layout.item_shop_manager_stats_order)
abstract class ManagerStatsModelTrendingHolder : EpoxyModelWithHolder<ManagerStatsModelTrendingHolder.Holder>() {
    @EpoxyAttribute
    internal lateinit var data: ManagerStatsModel.TrendingOrder

    override fun bind(holder: Holder) = holder.bindData(data)

    class Holder : BaseEpoxyHolder<ManagerStatsModel.TrendingOrder>() {
        @BindView(R.id.iv_manager_stats_trending_order_veg)
        internal lateinit var imItemStatus: ImageView
        @BindView(R.id.tv_manager_stats_trending_order_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_manager_stats_trending_order_revenue)
        internal lateinit var tvRevenue: TextView

        override fun bindData(data: ManagerStatsModel.TrendingOrder) {
            val item: MenuItemBriefModel = data.item;
            val revenueGenerated: Double = data.revenueContribution
            if (item.isVegetarian) imItemStatus.setImageResource(R.drawable.ic_veg)
            else imItemStatus.setImageResource(R.drawable.ic_non_veg)

            tvItemName.text = item.name;
            tvRevenue.text = String.format(Locale.getDefault(), "%.2f %% revenue contribution", revenueGenerated);
        }}
}