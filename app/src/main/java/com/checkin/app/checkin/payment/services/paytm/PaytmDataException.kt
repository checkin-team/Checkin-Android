package com.checkin.app.checkin.payment.services.paytm

class PaytmDataException(resultCode: String?, resultMsg: String?, resultStatus: String?) : Exception("Unable to get the requested data") {
    override val message: String = "${super.message}\n[resultCode=$resultCode, resultStatus=$resultStatus, resultMsg='$resultMsg']"
}