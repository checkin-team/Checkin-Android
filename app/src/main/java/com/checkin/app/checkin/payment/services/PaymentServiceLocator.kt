package com.checkin.app.checkin.payment.services

import androidx.appcompat.app.AppCompatActivity
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.services.razorpay.RazorpayPaymentService
import com.checkin.app.checkin.payment.services.razorpay.RazorpayTransactionService
import com.razorpay.Razorpay

object PaymentServiceLocator {
    fun getTransactionService(activity: AppCompatActivity, listener: TransactionListener): ITransactionService {
        return RazorpayTransactionService(activity, listener)
    }

    val paymentService: IPaymentService = RazorpayPaymentService
}

typealias Transaction = Razorpay