package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaxDetailModel(
        val cgst: String,
        val sgst: String,
        val igst: String
)
