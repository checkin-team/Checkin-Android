package com.checkin.app.checkin.payment.listeners

import com.checkin.app.checkin.payment.models.PAYMENT_TYPE
import com.checkin.app.checkin.payment.models.PaymentOptionModel

interface PaymentOptionSelectListener {
    fun onAddPaymentOption(pmtType: PAYMENT_TYPE)
    fun onPayPaymentOption(paymentOption: PaymentOptionModel)
}