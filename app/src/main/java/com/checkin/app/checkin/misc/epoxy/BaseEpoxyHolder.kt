package com.checkin.app.checkin.misc.epoxy

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyHolder

abstract class BaseEpoxyHolder<T> : EpoxyHolder() {
    lateinit var itemView: View

    val context: Context
        get() = itemView.context

    @CallSuper
    override fun bindView(itemView: View) {
        this.itemView = itemView
        ButterKnife.bind(this, itemView)
    }

    abstract fun bindData(data: T)
}
