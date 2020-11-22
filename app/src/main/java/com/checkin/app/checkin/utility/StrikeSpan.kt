package com.checkin.app.checkin.utility

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel

internal fun formatDiscountedPrice(oldCost: String, newCost: String): SpannableString {
    val ssb = SpannableStringBuilder(oldCost)
    val strikethroughSpan = StrikethroughSpan()
    ssb.setSpan(
            strikethroughSpan,
            ssb.length - oldCost.length,
            ssb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ssb.append("  ")
    ssb.append(newCost)
    val ss1 = SpannableString(ssb)
    ss1.setSpan(RelativeSizeSpan(.8f), 0, oldCost.length, 0)
    return ss1
}

fun SessionOrderedItemModel.formatPriceWithOff(context: Context): CharSequence {
    val originalCost: String = Utils.formatCurrencyAmount(context, originalCost)
    val actualCost: String = Utils.formatCurrencyAmount(context, cost)
    return if (isCostTampered) formatDiscountedPrice(originalCost, actualCost) else actualCost
}

fun OrderedItemModel.formatPriceWithOff(context: Context): CharSequence {
    val originalCost: String = Utils.formatCurrencyAmount(context, originalCost)
    val actualCost: String = Utils.formatCurrencyAmount(context, cost)
    return if (isCostTampered) formatDiscountedPrice(originalCost, actualCost) else actualCost
}