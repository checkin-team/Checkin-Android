package com.checkin.app.checkin.misc.exceptions

import java.io.IOException

class RequestCanceledException(cause: Throwable?) : IOException(cause) {
    override val message: String = "Request canceled before execution."
}