package com.checkin.app.checkin.Shop.ShopJoin

import android.text.TextUtils
import com.checkin.app.checkin.misc.models.LocationModel
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
class ShopJoinModel internal constructor() {
    @JsonProperty("name")
    private var name: String? = null
    @JsonProperty("gstin")
    private var gstin: String? = null
    @JsonProperty("location")
    private var location: LocationModel? = null
    @JsonProperty("phone_token")
    private var phoneToken: String? = null
    @JsonProperty("email")
    private var email: String? = null

    fun setName(name: String?) {
        this.name = name
    }

    fun setGstin(gstin: String?) {
        this.gstin = gstin
    }

    fun setLocation(location: LocationModel?) {
        this.location = location
    }

    fun setLocality(locality: String?) {
        location = location?.copy(locality = locality) ?: LocationModel(locality)
    }

    fun setPhoneToken(phoneToken: String?) {
        this.phoneToken = phoneToken
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    val isValidName: Boolean
        get() = name != null && !name!!.isEmpty()

    val isValidGstin: Boolean
        get() = gstin != null && !gstin!!.isEmpty()

    val isValidLocality: Boolean
        get() = location != null && !TextUtils.isEmpty(location!!.locality)
}