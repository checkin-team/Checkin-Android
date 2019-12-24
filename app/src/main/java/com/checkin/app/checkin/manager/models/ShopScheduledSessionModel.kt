package com.checkin.app.checkin.manager.models

import com.checkin.app.checkin.User.UserBriefModel
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ShopScheduledSessionModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        val created: Date,
        @JsonProperty("checked_in") val checkedIn: Date?,
        val scheduled: ScheduledSessionDetailModel,
        val amount: Double,
        @JsonProperty("is_pre_dining") val isPreDining: Boolean,
        val owner: UserBriefModel
)