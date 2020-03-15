package com.checkin.app.checkin.home.epoxy

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
    internal lateinit var locationSelectedListener: LocationSelectedListener

    override fun createNewHolder() = Holder(locationSelectedListener)

    override fun bind(holder: Holder) {
        holder.bindData(data)

    }

    class Holder(val locationSelectedListener: LocationSelectedListener) : BaseEpoxyHolder<CityLocationModel>() {

        @BindView(R.id.tv_user_location_city)
        internal lateinit var tvCityLocation: TextView

        @BindView(R.id.tv_user_location_state)
        internal lateinit var tvStateLocation: TextView


        override fun bindData(data: CityLocationModel) {
            tvCityLocation.text = data.name
            tvStateLocation.text = "${data.state} ${data.country}"

            itemView.setOnClickListener {
                locationSelectedListener.onLocationSelected(data)
            }

        }
    }
}