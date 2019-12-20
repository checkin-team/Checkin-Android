package com.checkin.app.checkin.restaurant.models

import com.checkin.app.checkin.misc.models.BriefModel
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class RestaurantBriefModel : BriefModel() {
    @Id(assignable = true)
    override var pk: Long = 0

    fun formatRestaurantName() = "Live at <font color=#0295aa>$displayName</font>"
}