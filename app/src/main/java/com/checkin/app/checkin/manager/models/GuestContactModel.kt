package com.checkin.app.checkin.manager.models

data class GuestContactModel(
        val phone: String,
        val name: String? = null,
        val email: String? = null
)