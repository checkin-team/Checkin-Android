package com.checkin.app.checkin.payment.models.razorpay

import com.checkin.app.checkin.payment.models.PaymentMethods
import com.fasterxml.jackson.annotation.JsonProperty

data class RazorpayPaymentMethod(
        val card: Boolean,
        @JsonProperty("debit_card") val debitCard: Boolean,
        @JsonProperty("credit_card") val creditCard: Boolean,
        @JsonProperty("card_networks") val cardNetworks: Map<String, Int>,
        val netbanking: Map<String, String>
) : PaymentMethods()