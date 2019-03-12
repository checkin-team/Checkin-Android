package com.checkin.app.checkin.Inventory.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class InventoryAvailabilityModel {

    @JsonProperty("pk")
    private long pk;
    private String detail;
    private boolean isAvailable;

    public InventoryAvailabilityModel() {
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

    @JsonProperty("is_available")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @JsonProperty("detail")
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
