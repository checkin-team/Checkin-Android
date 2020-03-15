package com.checkin.app.checkin.home.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.listeners.LocationSelectedListener
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_user_location_current)
abstract class CurrentLocationModelHolder : EpoxyModelWithHolder<CurrentLocationModelHolder.Holder>() {
    @EpoxyAttribute
    internal lateinit var locationSelectedListener: LocationSelectedListener

    override fun createNewHolder() = Holder(locationSelectedListener)

    override fun bind(holder: Holder) {
        holder.bindData("Current Location")
    }

    class Holder(val locationSelectedListener: LocationSelectedListener) : BaseEpoxyHolder<String>() {

        override fun bindData(data: String) {
            locationSelectedListener.onLocationSelected(null)

        }
    }
}