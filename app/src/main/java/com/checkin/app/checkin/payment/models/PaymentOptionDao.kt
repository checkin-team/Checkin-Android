package com.checkin.app.checkin.payment.models

import io.objectbox.Box

fun Box<UPICollectPaymentOptionModel>.getByVpa(vpa: String): UPICollectPaymentOptionModel? = query()
        .equal(UPICollectPaymentOptionModel_.vpa, vpa)
        .build()
        .findUnique()

fun Box<UPIPushPaymentOptionModel>.getByAppName(appName: String): UPIPushPaymentOptionModel? = query()
        .equal(UPIPushPaymentOptionModel_.appName, appName)
        .build()
        .findUnique()

fun Box<CardPaymentOptionModel>.getByCardNumber(cardNumber: String): CardPaymentOptionModel? = query()
        .equal(CardPaymentOptionModel_.cardNumber, cardNumber)
        .build()
        .findUnique()
