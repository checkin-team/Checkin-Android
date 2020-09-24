package com.checkin.app.checkin.Shop

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.epoxy.NearbyRestaurantModelHolder
import com.checkin.app.checkin.restaurant.activities.PublicRestaurantProfileActivity
import io.objectbox.BoxStore
import io.objectbox.BoxStore.context




    fun NearbyRestaurantModelHolder.changerating(ratings:Double, tvRating :TextView){
       rate(ratings.toFloat(),tvRating)
    }

fun PublicRestaurantProfileActivity.changerating(ratings:Float, tvRating :TextView){
    rate(ratings,tvRating)

}

fun rate(ratings:Float, tvrating :TextView){
    when(ratings){
        in 4.0..5.0 -> tvrating.setBackgroundResource(R.color.apple_green)
        in 3.0..4.0 ->  tvrating.setBackgroundResource(R.color.orange_red)
        else -> tvrating.setBackgroundResource(R.color.primary_red)

    }
}

/*fun ratingvalue(rating: Float,color: Int): Int {

    when(rating){
        in 4.0..5.0 -> color== R.color.apple_green
        in 3.0..4.0 -> color==R.color.orange_red
        else -> color == R.color.primary_red
    }
    return color
}

 */


