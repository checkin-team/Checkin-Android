package com.checkin.app.checkin.Inventory.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class InventoryAvailabilityModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("is_available")
    private boolean isAvailable;

    public InventoryAvailabilityModel() {
    }

    public InventoryAvailabilityModel(long pk, boolean isAvailable) {
        this.pk = pk;
        this.isAvailable = isAvailable;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
