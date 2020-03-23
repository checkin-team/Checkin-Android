package com.checkin.app.checkin.payment.services

import androidx.appcompat.app.AppCompatActivity
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.services.razorpay.RazorpayPaymentService
import com.checkin.app.checkin.payment.services.razorpay.RazorpayTransactionService
import com.checkin.app.checkin.utility.ConflatedSingletonHolder
import com.razorpay.Razorpay

class PaymentServiceLocator private constructor(val activity: AppCompatActivity) {
    val paymentProvider: PaymentProvider by lazy {
        Razorpay(activity, RemoteConfig[RemoteConfig.Constants.KEY_RAZORPAY].asString())
    }

    fun getTransactionService(listener: TransactionListener): ITransactionService {
        return RazorpayTransactionService(paymentProvider, listener)
    }

    val paymentService: IPaymentService = RazorpayPaymentService

    companion object : ConflatedSingletonHolder<PaymentServiceLocator, AppCompatActivity>({ PaymentServiceLocator(it) })
}

typealias PaymentProvider = Razorpay

val AppCompatActivity.paymentLocator
    get() = PaymentServiceLocator.getInstance(this)
