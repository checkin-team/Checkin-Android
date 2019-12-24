package com.checkin.app.checkin.misc.holders

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_text)
abstract class TextModelHolder : EpoxyModelWithHolder<TextModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var text: String

    override fun bind(holder: Holder) = holder.bindData(text)

    class Holder : BaseEpoxyHolder<String>() {
        @BindView(R.id.tv_text_value)
        internal lateinit var tvValue: TextView

        override fun bindData(data: String) {
            tvValue.text = data
        }
    }
}