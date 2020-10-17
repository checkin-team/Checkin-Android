package com.checkin.app.checkin.Shop

import android.widget.TextView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.epoxy.NearbyRestaurantModelHolder
import com.checkin.app.checkin.restaurant.activities.PublicRestaurantProfileActivity


fun PublicRestaurantProfileActivity.changetextviewcolor(rating: Float,tvrating: TextView) {
    ratingvalue(rating, tvrating)


}

fun NearbyRestaurantModelHolder.changetextviewcolor(rating: Double, tvrating: TextView) {
    ratingvalue(rating.toFloat(), tvrating)


}


 private fun ratingvalue(rating: Float,tvrating: TextView) {

     when(rating){
         in 4.0..5.0 -> tvrating.setBackgroundResource(R.color.apple_green)
         in 3.0..4.0 ->  tvrating.setBackgroundResource(R.color.md_deep_orange_300)
         else -> tvrating.setBackgroundResource(R.color.red_500)

     }
}

