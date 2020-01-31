package com.checkin.app.checkin.misc.holders

import android.view.View
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.utility.pass
import com.facebook.shimmer.ShimmerFrameLayout

@EpoxyModelClass(layout = R.layout.shimmer, useLayoutOverloads = true)
abstract class ShimmerModelHolder : EpoxyModelWithHolder<ShimmerModelHolder.Holder>() {
    class Holder : BaseEpoxyHolder<Any>() {
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            if (itemView is ShimmerFrameLayout) itemView.startShimmer()
        }

        override fun bindData(data: Any) = pass
    }
}
