package com.checkin.app.checkin.Shop.ShopJoin;

import com.checkin.app.checkin.Misc.LocationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopJoinModel {
    @JsonProperty("name") private String name;
    @JsonProperty("gstin") private String gstin;
    @JsonProperty("location") private LocationModel location;
    @JsonProperty("locality") private String locality;
    @JsonProperty("phone_token") private String phoneToken;
    @JsonProperty("email") private String email;

    ShopJoinModel() {}

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
        this.locality = locality;
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
        return locality != null && !locality.isEmpty();
    }
}
