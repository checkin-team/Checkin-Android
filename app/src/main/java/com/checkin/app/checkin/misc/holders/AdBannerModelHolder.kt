package com.checkin.app.checkin.misc.holders

import android.widget.ImageView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.utility.Utils

@EpoxyModelClass(layout = R.layout.item_ad_banner, useLayoutOverloads = true)
abstract class AdBannerModelHolder : EpoxyModelWithHolder<AdBannerModelHolder.Holder>() {
    @EpoxyAttribute
    var imageUrl: String? = null

    @EpoxyAttribute
    var defaultDrawable: Int = 0

    override fun bind(holder: Holder) = holder.bindData(imageUrl!!)

    inner class Holder : BaseEpoxyHolder<String>() {
        @BindView(R.id.im_item_banner)
        internal lateinit var imBanner: ImageView

        override fun bindData(data: String) {
            Utils.loadImageOrDefault(imBanner, data, defaultDrawable)
        }
    }
}