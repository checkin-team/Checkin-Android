package com.checkin.app.checkin.accounts

import android.content.Context
import android.preference.PreferenceManager
import com.checkin.app.checkin.utility.Constants

object AccountUtil {
    fun getUsername(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(Constants.SP_USER_PROFILE_NAME, "")
}
