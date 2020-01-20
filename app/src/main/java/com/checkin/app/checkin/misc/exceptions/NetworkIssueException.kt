package com.checkin.app.checkin.misc.exceptions

import java.io.IOException

class NetworkIssueException(cause: Throwable?) : IOException(cause) {
    override val message: String = "Something went wrong with internet. Please try again later."
}