package com.checkin.app.checkin.home.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.listeners.LocationSelectedListener
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_user_location_current)
abstract class CurrentLocationModelHolder : EpoxyModelWithHolder<CurrentLocationModelHolder.Holder>() {
    @EpoxyAttribute
    internal lateinit var listener: LocationSelectedListener

    override fun createNewHolder() = Holder(listener)

    override fun bind(holder: Holder) = holder.bindData("Current Location")

    class Holder(val listener: LocationSelectedListener) : BaseEpoxyHolder<String>() {
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnClickListener { listener.onLocationSelected(null) }
        }

        override fun bindData(data: String) {}
    }
}