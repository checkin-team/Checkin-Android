package com.checkin.app.checkin.misc.exceptions

import com.fasterxml.jackson.core.JsonProcessingException

class JacksonProcessingException(cause: Throwable?) : JsonProcessingException(cause) {
    override val message: String = "An unexpected error has occurred."
}