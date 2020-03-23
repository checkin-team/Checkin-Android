package com.checkin.app.checkin.payment.services.razorpay

import com.checkin.app.checkin.payment.services.TransactionException

class RazorpayTransactionException(errCode: Int, errMsg: String?) : TransactionException(errCode, errMsg)