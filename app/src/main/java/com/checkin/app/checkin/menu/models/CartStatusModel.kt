package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.Shop.RestaurantLocationModel
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
data class CartStatusModel(
        @Id(assignable = true) var pk: Long,
        @JsonIgnore val restaurant: ToOne<RestaurantLocationModel>
) {
    @JsonProperty("restaurant")
    fun setRestaurantInfo(restaurantLocationModel: RestaurantLocationModel) {
        restaurant.setAndPutTarget(restaurantLocationModel)
    }
}
