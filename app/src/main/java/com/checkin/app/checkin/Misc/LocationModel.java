package com.checkin.app.checkin.Misc;

import android.location.Address;

import com.checkin.app.checkin.Data.Converters;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import androidx.annotation.NonNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class LocationModel {
    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("address")
    private String address;

    @JsonProperty("pincode")
    private String pinCode;

    @JsonProperty("country")
    private String countryCode;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("locality")
    private String locality;

    public LocationModel() {}

    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationModel(Address address) {
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.state = address.getAdminArea();
        this.address = address.getAddressLine(0);
        this.pinCode = address.getPostalCode();
        this.countryCode = address.getCountryCode();
        this.locality = address.getLocality() == null ? address.getFeatureName() : address.getLocality();
        this.city = address.getSubAdminArea() == null ? this.locality : address.getSubAdminArea();
    }

    public LocationModel(String locality) {
        this.locality = locality;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @JsonProperty("point")
    public ObjectNode getPoint() {
        return Converters.objectMapper.createObjectNode()
                .put("latitude", latitude)
                .put("longitude", longitude);
    }

    @NonNull
    @Override
    public String toString() {
        return locality == null ? address : locality;
    }
}
