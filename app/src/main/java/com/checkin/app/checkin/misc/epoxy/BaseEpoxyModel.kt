package com.checkin.app.checkin.misc.epoxy

import android.view.View
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyModel

abstract class BaseEpoxyModel<T : View> : EpoxyModel<T>() {
    override fun bind(view: T) {
        super.bind(view)
        ButterKnife.bind(this, view)
    }
}
