package com.checkin.app.checkin.data.db

import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel_
import io.objectbox.Box

fun Box<UPICollectPaymentOptionModel>.getByVpa(vpa: String): UPICollectPaymentOptionModel? = query()
        .equal(UPICollectPaymentOptionModel_.vpa, vpa)
        .build()
        .findUnique()
