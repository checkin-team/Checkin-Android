package com.checkin.app.checkin.data.notifications

import android.content.Intent

object DeepLinkUtil {
    fun getEndpointArgs(intent: Intent, urlRegex: Regex): MatchGroupCollection? {
        val path = intent.data?.path ?: return null
        return urlRegex.find(path)?.groups
    }
}