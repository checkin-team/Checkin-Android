package com.checkin.app.checkin.data.config

import com.checkin.app.checkin.utility.Constants.IS_RELEASE_BUILD
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsUtil {
    fun setAppBuild(analytics: FirebaseAnalytics) {
        val type = if (IS_RELEASE_BUILD()) "PROD" else "STAGING"
        analytics.setUserProperty(Constants.APP_BUILD_TYPE, type)
    }

    object Constants {
        const val APP_BUILD_TYPE = "checkin_build_type"
    }
}