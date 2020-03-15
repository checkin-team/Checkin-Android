package com.checkin.app.checkin.home.epoxy

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.listeners.LocationSelectedListener
import com.checkin.app.checkin.home.model.CityLocationModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_user_location_city)
abstract class CityLocationModelHolder : EpoxyModelWithHolder<CityLocationModelHolder.Holder>() {
    @EpoxyAttribute
    internal lateinit var data: CityLocationModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    internal lateinit var listener: LocationSelectedListener

    override fun createNewHolder() = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData(data)

    class Holder(val listener: LocationSelectedListener) : BaseEpoxyHolder<CityLocationModel>() {
        @BindView(R.id.tv_user_location_city)
        internal lateinit var tvCityLocation: TextView
        @BindView(R.id.tv_user_location_state)
        internal lateinit var tvStateLocation: TextView

        private lateinit var mData: CityLocationModel

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onLocationSelected(mData) }
        }

        override fun bindData(data: CityLocationModel) {
            mData = data
            tvCityLocation.text = data.name
            tvStateLocation.text = "${data.state} ${data.country}"
        }
    }
}