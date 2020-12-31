package com.checkin.app.checkin.misc.epoxy

import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.checkin.app.checkin.utility.Utils

abstract class BaseEpoxyController : EpoxyController() {
    override fun onExceptionSwallowed(exception: RuntimeException) {
        super.onExceptionSwallowed(exception)
        Log.e(TAG, exception.message, exception)
        Utils.logErrors(TAG, exception)
    }

    companion object {
        private val TAG = BaseEpoxyController::class.simpleName
    }
}
