package com.checkin.app.checkin.user.models

import com.checkin.app.checkin.misc.models.BriefModel
import com.fasterxml.jackson.annotation.JsonProperty

class UserBriefModel : BriefModel() {
    @JsonProperty("phone_no")
    var phoneNo: String? = null
}