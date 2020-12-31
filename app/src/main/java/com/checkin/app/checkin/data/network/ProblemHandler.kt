package com.checkin.app.checkin.data.network

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.ProblemModel.ERROR_CODE
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.activities.UpdateAppActivity
import com.checkin.app.checkin.utility.Utils

/**
 * Created by shivanshs9 on 27/5/19.
 */
object ProblemHandler {
    private val TAG = ProblemHandler::class.java.simpleName
    fun handleDeprecatedAPIUse(context: Context, resource: Resource<*>): Boolean {
        val problemModel = resource.problem
        if (problemModel != null) {
            if (problemModel.getErrorCode() === ERROR_CODE.DEPRECATED_VERSION || problemModel.getErrorCode() === ERROR_CODE.INVALID_VERSION) {
                Utils.crashlytics.log(String.format("E/%s: Error Code: %s", TAG, problemModel.getErrorCode()))
                context.startActivity(Intent.makeRestartActivityTask(ComponentName(context, UpdateAppActivity::class.java)))
                return true
            }
        }
        return false
    }

    fun handleUnauthenticatedAPIUse(context: Context, resource: Resource<*>): Boolean {
        if (resource.status === Resource.Status.ERROR_UNAUTHORIZED) {
            Utils.logoutFromApp(context)
            Utils.toast(context, R.string.app_force_logged_out)
            return true
        }
        return false
    }

    fun handleProblems(context: Context, resource: Resource<*>?): Boolean = resource?.let {
        handleDeprecatedAPIUse(context, it) || handleUnauthenticatedAPIUse(context, it)
    } ?: false
}