package com.checkin.app.checkin.restaurant.epoxy

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_public_restaurant)
abstract class PublicRestaurantInsiderModel : EpoxyModelWithHolder<PublicRestaurantInsiderModel.TextHolder>() {


    @EpoxyAttribute
    lateinit var text: String


    override fun bind(holder: TextHolder) = holder.bindData(text)


    inner class TextHolder : BaseEpoxyHolder<String>() {

        @BindView(R.id.tv_public_resturant_text)
        lateinit var tvText: TextView

        override fun bindData(data: String) {
            tvText.text = data
        }

    }

}