package com.checkin.app.checkin.payment.listeners

import com.checkin.app.checkin.payment.models.TransactionResponseModel

interface TransactionListener {
    fun onTransactionResponse(data: TransactionResponseModel)
    fun onTransactionCancel(msg: String?)
    fun onTransactionError(error: Throwable)
}