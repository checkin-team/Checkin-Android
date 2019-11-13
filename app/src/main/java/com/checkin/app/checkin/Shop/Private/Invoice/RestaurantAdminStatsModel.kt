package com.checkin.app.checkin.Shop.Private.Invoice

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class RestaurantAdminStatsModel {
    @JsonProperty("total_sales")
    var totalSales: Double = 0.0

    @JsonProperty("total_orders")
    var totalOrders: Int = 0

    @JsonProperty("total_discounts")
    var totalDiscounts: Double = 0.0

    @JsonProperty("total_taxes")
    var totalTaxes: Double = 0.0

    fun formatTotalOrders() = totalOrders.toString()
}