package com.checkin.app.checkin.misc.holders;

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.checkin.app.checkin.utility.GridItemDecoration
import com.checkin.app.checkin.utility.SpanCountLookup

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class VerticalGridCarousel(context: Context) : Carousel(context) {
    private var spanSizeCalculator: SpanCountLookup = { DEFAULT_SPAN_COUNT to 0 }

    @ModelProp(ModelProp.Option.DoNotHash)
    fun setSpanSizeCalculator(spanCalculator: SpanCountLookup?) {
        if (spanCalculator != null) spanSizeCalculator = spanCalculator
    }

    @ModelProp(ModelProp.Option.DoNotHash)
    @JvmField
    var maxSpanCount: Int = DEFAULT_SPAN_COUNT

    @ModelProp(ModelProp.Option.DoNotHash)
    @JvmField
    var itemSpacing: Int = 0

    override fun createLayoutManager(): LayoutManager = GridLayoutManager(context, maxSpanCount, GridLayoutManager.VERTICAL, false).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = maxSpanCount / spanSizeCalculator(position).first
        }
        addItemDecoration(GridItemDecoration(orientation, itemSpacing, spanSizeCalculator))
    }

    companion object {
        const val DEFAULT_SPAN_COUNT = 2
    }
}
