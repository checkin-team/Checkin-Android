package com.checkin.app.checkin.payment.listeners

import com.checkin.app.checkin.payment.models.TransactionResponseModel
import com.checkin.app.checkin.payment.services.TransactionException

interface TransactionListener {
    fun onTransactionResponse(data: TransactionResponseModel)
    fun onTransactionCancel(msg: String?)
    fun onTransactionError(error: TransactionException)
}