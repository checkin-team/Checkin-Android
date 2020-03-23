package com.checkin.app.checkin.payment.services

open class TransactionException(val errCode: Int, val errMsg: String?) : Exception("Unable to perform the transaction") {
    override val message: String = "${super.message}\n[errorCode=$errCode, errorMsg='$errMsg']"

    override fun getLocalizedMessage(): String = errMsg ?: message
}