package com.checkin.app.checkin.home.epoxy

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.model.NearbyRestaurantModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.blackAndWhite

@EpoxyModelClass(layout = R.layout.item_home_restaurant_banner)
abstract class NearbyRestaurantModelHolder : EpoxyModelWithHolder<NearbyRestaurantModelHolder.Holder>() {

    @EpoxyAttribute
    internal lateinit var restaurantModel: NearbyRestaurantModel

    override fun bind(holder: Holder) = holder.bindData(restaurantModel)

    inner class Holder : BaseEpoxyHolder<NearbyRestaurantModel>() {
        @BindView(R.id.im_restaurant_banner_cover)
        internal lateinit var imRestaurantBanner: ImageView
        @BindView(R.id.tv_restaurant_banner_cuisines)
        internal lateinit var tvCuisines: TextView
        @BindView(R.id.tv_restaurant_banner_rating)
        internal lateinit var tvRating: TextView
        @BindView(R.id.tv_restaurant_banner_name_locality)
        internal lateinit var tvRestaurantName: TextView
        @BindView(R.id.tv_restaurant_banner_distance)
        internal lateinit var tvDistance: TextView
        @BindView(R.id.tv_restaurant_banner_offer_code)
        internal lateinit var tvCouponCode: TextView
        @BindView(R.id.tv_restaurant_banner_count_checkins)
        internal lateinit var tvCountCheckins: TextView
        @BindView(R.id.tv_restaurant_banner_offer_special)
        internal lateinit var tvExclusiveOffer: TextView
        @BindView(R.id.tv_restaurant_banner_offer_summary)
        internal lateinit var tvOfferSummary: TextView
        @BindView(R.id.im_restaurant_banner_distance)
        internal lateinit var imDistance: ImageView
        @BindView(R.id.container_restaurant_banner_offer)
        internal lateinit var containerOffer: ViewGroup
        @BindView(R.id.cl_restaurant_banner_location)
        internal lateinit var clLocation: ConstraintLayout
        @BindView(R.id.tv_restaurant_banner_opening_timings)
        internal lateinit var tvOpenTimings: TextView

        override fun bindData(data: NearbyRestaurantModel) {
            itemView.setOnClickListener {
                data.let { itemView.context.openPublicRestaurantProfile(it.pk) }
            }

            tvCuisines.text = data.cuisines.let {
                if (it.isNotEmpty()) {
                    val maxLen = it.size.coerceAtMost(3)
                    it.slice(0 until maxLen).joinToString(" • ")
                } else "-"
            }

            tvRating.text = data.formatRating
             if(data.ratings in 4.0..5.0)
            {

                tvRating.setBackgroundColor(ContextCompat.getColor(context,R.color.md_green_400));

            }
           else if(data.ratings in 3.0..4.0){
                tvRating.setBackgroundColor(ContextCompat.getColor(context,R.color.md_deep_orange_300));

            }
            else if(data.ratings<=3.0){
                tvRating.setBackgroundColor(ContextCompat.getColor(context,R.color.red_500));


            }
            tvDistance.text = data.formatDistance

            imDistance.setImageResource(if (data.distance > 1.5) R.drawable.ic_distance_vehicle else R.drawable.ic_distance_walking)

            tvRestaurantName.text = data.locality.let {
                if (it != null) data.name + " - " + data.locality
                else data.name
            }

            tvCountCheckins.text = data.formatCheckins

            data.offer.let {
                if (it == null) containerOffer.visibility = View.GONE
                else {
                    containerOffer.visibility = View.VISIBLE
                    tvCouponCode.text = Utils.fromHtml(itemView.context.getString(R.string.discount_code, it.code))
                    tvExclusiveOffer.visibility = if (it.isGlobal) View.GONE else View.VISIBLE
                    tvOfferSummary.text = Utils.fromHtml(it.name)
                }
            }

            data.covers.let {
                if (it.getOrNull(0) != null) Utils.loadImageOrDefault(imRestaurantBanner, it[0], R.drawable.cover_restaurant_unknown)
                else imRestaurantBanner.setImageResource(R.drawable.cover_restaurant_unknown)
            }
            if (data.isOpen) {
                imRestaurantBanner.colorFilter = null
                clLocation.visibility = View.VISIBLE
                tvOpenTimings.visibility = View.GONE
            } else {
                imRestaurantBanner.blackAndWhite()
                clLocation.visibility = View.GONE
                tvOpenTimings.visibility = View.VISIBLE
                tvOpenTimings.text = data.timings.formatDescription(itemView.context)
            }
        }
    }
}