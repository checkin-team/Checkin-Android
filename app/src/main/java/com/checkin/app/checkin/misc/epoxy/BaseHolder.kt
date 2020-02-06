package com.checkin.app.checkin.misc.epoxy

import android.view.View
import androidx.annotation.CallSuper
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyHolder

abstract class BaseHolder : EpoxyHolder() {

    @CallSuper
    override fun bindView(itemView: View) {
        ButterKnife.bind(this, itemView)
    }

}