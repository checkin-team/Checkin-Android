package com.checkin.app.checkin.Account

import android.content.Context
import android.preference.PreferenceManager
import com.checkin.app.checkin.Utility.Constants

object AccountUtil {
    fun getUsername(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(Constants.SP_USER_PROFILE_NAME, "")
}
