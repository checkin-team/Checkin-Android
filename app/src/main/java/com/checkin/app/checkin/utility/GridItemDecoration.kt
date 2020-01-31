package com.checkin.app.checkin.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation

class GridItemDecoration(
        @Orientation val orientation: Int,
        private val itemSpacing: Int,
        val spanCountLookup: SpanCountLookup
) : RecyclerView.ItemDecoration() {
    var itemCount: Int = 0

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        itemCount = parent.adapter?.itemCount ?: 0
        val position = parent.getChildAdapterPosition(view)
        if (orientation == RecyclerView.HORIZONTAL) {
            getHorizontalOffsets(outRect, position)
        } else {
            getVerticalOffsets(outRect, position)
        }
    }

    private fun getHorizontalOffsets(outRect: Rect, position: Int) {
        outRect.bottom = if (isEndItem(position)) 0 else itemSpacing
        outRect.right = if (isInFinalBank(position)) 0 else itemSpacing
    }

    private fun isInFinalBank(position: Int): Boolean = spanCountLookup(itemCount - 1).let {
        position >= itemCount - it.first
    }

    private fun getVerticalOffsets(outRect: Rect, position: Int) {
        outRect.right = if (isEndItem(position)) 0 else itemSpacing
        outRect.bottom = if (isInFinalBank(position)) 0 else itemSpacing
    }

    private fun isEndItem(position: Int): Boolean = spanCountLookup(position).let {
        (position - it.second) % it.first != 0
    }
}

typealias SpanCount = Int
typealias StartPosition = Int
typealias SpanCountLookup = (position: Int) -> Pair<SpanCount, StartPosition>
