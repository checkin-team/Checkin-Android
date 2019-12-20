package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.session.models.SessionBillModel

data class CartBillModel(
        val pk: Long,
        val bill: SessionBillModel
)