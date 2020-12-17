package com.checkin.app.checkin.manager.models



data class SimpleModelHeading(val header:String,val type: String)
fun getHeading() = mutableListOf<SimpleModelHeading>(

        SimpleModelHeading(  "TrendingItems","TRENDING ITEMS IN LAST 30 DAYS"),
        SimpleModelHeading("PoorItems","POOR ITEMS IN LAST 30 DAYS"),
        SimpleModelHeading("TrendingGroups","TREINDING GROUPS IN LAST 30 DAYS")




)