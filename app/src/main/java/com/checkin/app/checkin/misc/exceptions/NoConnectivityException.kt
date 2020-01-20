package com.checkin.app.checkin.misc.exceptions

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String = "No network available, please check your WiFi or Data connection."
}