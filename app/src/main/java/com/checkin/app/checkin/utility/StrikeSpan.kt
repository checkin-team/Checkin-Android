package com.checkin.app.checkin.utility

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan



fun strikeThrough(originalCost:String,actualcost:String): SpannableString {
    val ssb = SpannableStringBuilder(originalCost)
    val strikethroughSpan = StrikethroughSpan()
    ssb.setSpan(
            strikethroughSpan,
            ssb.length - originalCost.length,
            ssb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ssb.append("  ")
    ssb.append(actualcost)
    val ss1 = SpannableString(ssb)
    ss1.setSpan(RelativeSizeSpan(.8f),0,originalCost.length,0 )

   return ss1

}