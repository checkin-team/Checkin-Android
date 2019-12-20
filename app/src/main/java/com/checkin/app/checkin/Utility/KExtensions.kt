package com.checkin.app.checkin.Utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.checkin.app.checkin.misc.models.GeolocationModel
import com.checkin.app.checkin.misc.models.LocationModel
import com.google.android.material.tabs.TabLayout

fun TabLayout.setTabBackground(@ColorInt color: Int) = (0 until tabCount).forEach {
    getTabAt(it)?.view?.setBackgroundColor(color)
}

internal fun internalNavigateToLocation(context: Context, latitude: Double, longitude: Double) {
    val gmmIntentUri = Uri.parse("google.navigation:q=${latitude},${longitude}")
    Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        `package` = "com.google.android.apps.maps"
        ContextCompat.startActivity(context, this, null)
    }
}

fun LocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)

fun GeolocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)
