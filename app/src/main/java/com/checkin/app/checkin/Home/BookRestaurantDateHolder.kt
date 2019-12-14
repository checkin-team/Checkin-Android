package com.checkin.app.checkin.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.checkin.app.checkin.R
import io.objectbox.BoxStore.context

class BookRestaurantDateHolder(context: Context?) {
        lateinit var day:TextView
        lateinit var date:TextView
   lateinit var view:View
    init{
       view =LayoutInflater.from(context).inflate(R.layout.item_book_restaurant_day_date,null,false)
        day=view.findViewById(R.id.day)
        date=view.findViewById(R.id.date)
    }

    fun getViewArrayList():View{


        return view
    }
    fun bindView( dayText:String,dateText:String){
        day.text=dayText
        date.text=dateText
    }





}