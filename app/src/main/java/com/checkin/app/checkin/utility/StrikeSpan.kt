package com.checkin.app.checkin.utility

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.widget.TextView
import com.checkin.app.checkin.home.epoxy.InvoiceClosedOrderModelHolder
import com.checkin.app.checkin.session.activesession.InvoiceOrdersAdapter


fun InvoiceOrdersAdapter.StrikethroughSpaninvoice(originalCost:String,discountcost:String): CharSequence? {

    return Strikethrough(originalCost, discountcost)

}

public fun InvoiceClosedOrderModelHolder.StrikethroughSpaninvoice(originalCost:String,discountcost:String): CharSequence? {

    return Strikethrough(originalCost, discountcost)

}

fun Strikethrough(originalCost:String,discountcost:String): SpannableString {
    val ssb = SpannableStringBuilder(originalCost)
    val strikethroughSpan = StrikethroughSpan()
    ssb.setSpan(
            strikethroughSpan,
            ssb.length - originalCost.length,
            ssb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ssb.append("  ")
    ssb.append(discountcost)
    val ss1 = SpannableString(ssb)
    ss1.setSpan(RelativeSizeSpan(.8f),0,originalCost.length,0 )

   return ss1

}