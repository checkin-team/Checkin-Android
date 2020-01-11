package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.restaurant.models.RestaurantBriefModel
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
data class CartStatusModel(
        @Id(assignable = true) var pk: Long
) {

    @JsonProperty("restaurant")
    fun setRestaurantInfo(data: RestaurantBriefModel) {
        AppDatabase.getCartStatusModel(null).attach(this)
        AppDatabase.getRestaurantBriefModel(null).put(data)
        this.restaurant.target = data
    }

    @JsonIgnore
    lateinit var restaurant: ToOne<RestaurantBriefModel>
}
