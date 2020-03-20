package com.checkin.app.checkin.payment.services.razorpay

class RazorpayDataException(msg: String?) : Exception(msg ?: "Some error occurred using Razorpay")