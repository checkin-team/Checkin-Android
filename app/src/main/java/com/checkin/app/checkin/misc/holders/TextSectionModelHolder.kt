package com.checkin.app.checkin.misc.holders

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_section_heading, useLayoutOverloads = true)
abstract class TextSectionModelHolder : EpoxyModelWithHolder<TextSectionModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var heading: String

    override fun bind(holder: Holder) = holder.bindData(heading)

    class Holder : BaseEpoxyHolder<String>() {
        @BindView(R.id.tv_section_heading)
        internal lateinit var tvHeading: TextView

        override fun bindData(data: String) {
            tvHeading.text = data
        }
    }
}