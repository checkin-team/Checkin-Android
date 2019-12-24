package com.checkin.app.checkin.session.models

import android.content.Context
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.restaurant.models.RestaurantBriefModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class ActiveSessionModel {
    @JsonProperty("pk")
    val pk: Int = 0

    @JsonProperty("bill")
    var bill: Double = 0.toDouble()

    @JsonProperty("customers")
    lateinit var customers: MutableList<SessionCustomerModel>
        private set

    @JsonProperty("restaurant")
    lateinit var restaurant: RestaurantBriefModel
        private set

    @JsonProperty("checked_in")
    lateinit var checkedIn: Date

    @JsonProperty("checked_out")
    val checkedOut: Date? = null

    @JsonProperty("host")
    var host: BriefModel? = null

    @JsonProperty("is_public")
    val isCheckinPublic: Boolean = false

    @JsonProperty("is_requested_checkout")
    var isRequestedCheckout: Boolean = false

    val shopPk: Long by lazy { restaurant.pk }

    fun addCustomer(customerModel: SessionCustomerModel) = customers.add(0, customerModel)

    fun formatBill(context: Context) = "Total: ${Utils.formatCurrencyAmount(context, bill)}"
}
