package com.checkin.app.checkin.Shop.ShopJoin;

import android.text.TextUtils;

import com.checkin.app.checkin.misc.models.LocationModel;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShopJoinModel {
    @JsonProperty("name")
    private String name;

    @JsonProperty("gstin")
    private String gstin;

    @JsonProperty("location")
    private LocationModel location;

    @JsonProperty("phone_token")
    private String phoneToken;

    @JsonProperty("email")
    private String email;

    ShopJoinModel() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public void setLocality(String locality) {
        if (this.location == null) this.location = new LocationModel(locality);
        else this.location.setLocality(locality);
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValidName() {
        return name != null && !name.isEmpty();
    }

    public boolean isValidGstin() {
        return gstin != null && !gstin.isEmpty();
    }

    public boolean isValidLocality() {
        return location != null && !TextUtils.isEmpty(location.getLocality());
    }
}
