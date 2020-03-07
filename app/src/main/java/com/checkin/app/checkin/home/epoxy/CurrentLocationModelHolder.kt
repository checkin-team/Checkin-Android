package com.checkin.app.checkin.home.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_user_location_current)
abstract class CurrentLocationModelHolder : EpoxyModelWithHolder<CurrentLocationModelHolder.Holder>() {

    @EpoxyAttribute
    internal lateinit var currentListener: View.OnClickListener

    override fun bind(holder: Holder) {
        if (::currentListener.isInitialized)
            holder.itemView.setOnClickListener(currentListener)
    }

    class Holder : BaseEpoxyHolder<String>() {
        override fun bindData(data: String) {

        }
    }
}