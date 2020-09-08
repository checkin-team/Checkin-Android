package com.checkin.app.checkin.home.epoxy

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.model.ReferralModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.utility.Utils
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.item_referral)
abstract class ReferralModelHolder : EpoxyModelWithHolder<ReferralModelHolder.Holder>() {

    @EpoxyAttribute
    internal lateinit var referralModel: ReferralModel

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.bindData(referralModel)
    }

    inner class Holder : BaseEpoxyHolder<ReferralModel>() {
        @BindView(R.id.im_referral_restaurant)
        internal lateinit var imRestaurant: CircleImageView

        @BindView(R.id.tv_referral_name)
        internal lateinit var tvReferralName: TextView

        @BindView(R.id.tv_referral_description)
        internal lateinit var tvReferralDescription: TextView

        override fun bindData(data: ReferralModel) {
            Utils.loadImageOrDefault(imRestaurant, data.imageUrl, R.drawable.cover_restaurant_unknown)
            tvReferralName.text = data.restaurantName
            tvReferralDescription.text = data.offerDescription
        }

    }
}