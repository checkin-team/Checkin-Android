package com.checkin.app.checkin.payment.services.paytm

class PaytmTransactionException(errCode: Int, errMsg: String?) : Exception("Unable to perform the transaction") {
    override val message: String = "${super.message}\n[errorCode=$errCode, errorMsg='$errMsg']"
}