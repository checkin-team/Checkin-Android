package com.checkin.app.checkin.manager.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PreparationTimeModel(
        @JsonProperty("preparation_dispatch") val preparationTime: Long,
        val pk: Long = 0
)