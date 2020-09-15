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
        if(ratings in 4.0..5.0)
        {

            tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.apple_green));

        }
        else if(ratings in 3.0..4.0){
            tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.orange_red));

        }
        else if(ratings< 3.0){
            tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.primary_red));


        }
    }

fun PublicRestaurantProfileActivity.changerating(ratings:Float, tvRating :TextView){
    if(ratings in 4.0..5.0)
    {

        tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.apple_green));

    }
    else if(ratings in 3.0..4.0){
        tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.orange_red));

    }
    else if(ratings< 3.0){
        tvRating.setBackgroundColor(ContextCompat.getColor(context as Context, R.color.primary_red));


    }
}




