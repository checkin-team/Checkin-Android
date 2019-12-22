package com.checkin.app.checkin.User

import com.checkin.app.checkin.misc.models.BriefModel
import com.fasterxml.jackson.annotation.JsonProperty

class UserBriefModel : BriefModel() {
    @JsonProperty("phone_no")
    var phoneNo: String? = null
}