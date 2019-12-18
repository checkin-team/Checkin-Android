package com.checkin.app.checkin.misc.holders

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.ModelGroupHolder
import com.checkin.app.checkin.R
import com.facebook.shimmer.ShimmerFrameLayout

class ShimmerVerticalModelGroup(numOfShimmers: Int, modelInitializer: ModelInitializer) : EpoxyModelGroup(R.layout.model_shimmer_vertical, buildModels(numOfShimmers, modelInitializer)) {
    override fun bind(holder: ModelGroupHolder) {
        super.bind(holder)
        (holder.rootView as ShimmerFrameLayout).startShimmer()
    }

    companion object {
        fun buildModels(numOfShimmers: Int, modelInitializer: ModelInitializer): List<EpoxyModel<*>> = (0 until numOfShimmers).map {
            ShimmerModelHolder_().modelInitializer(it)
        }
    }
}

typealias ModelInitializer = ShimmerModelHolder.(Int) -> EpoxyModel<*>
