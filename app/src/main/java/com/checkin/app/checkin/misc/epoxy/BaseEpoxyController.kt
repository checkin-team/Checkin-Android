package com.checkin.app.checkin.misc.epoxy

import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.crashlytics.android.Crashlytics

abstract class BaseEpoxyController : EpoxyController() {
    override fun onExceptionSwallowed(exception: RuntimeException) {
        super.onExceptionSwallowed(exception)
        Log.e(TAG, exception.message, exception)
        Crashlytics.log(Log.ERROR, TAG, exception.message)
        Crashlytics.logException(exception)
    }

    companion object {
        private val TAG = BaseEpoxyController::class.simpleName
    }
}
