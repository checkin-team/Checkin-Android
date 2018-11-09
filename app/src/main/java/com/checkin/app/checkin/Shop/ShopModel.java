package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.Misc.LocationModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopModel {
    @JsonProperty("pk")
    protected String pk;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("tagline")
    protected String tagline;

    @JsonProperty("logo")
    protected String logo;

    @JsonProperty("covers")
    protected String[] covers;

    @JsonProperty("phone")
    protected String phone;

    @JsonProperty("email")
    protected String email;

    @JsonProperty("website")
    protected String website;

    @JsonProperty("gstin")
    protected String gstIn;

    protected PAYMENT_MODE[] paymentModes;

    @JsonProperty("location")
    protected LocationModel location;
    @JsonProperty("locality")
    protected String locality;

    @JsonProperty("extra_data")
    protected String[] extraData;

    @JsonProperty("is_verified")
    protected boolean verified;

    @JsonProperty("is_active")
    protected boolean active;

    public enum PAYMENT_MODE {
        CASH("csh"), PAYTM("ptm"), CARD("crd");

        String tag;

        PAYMENT_MODE(String tag) {
            this.tag = tag;
        }

        public static PAYMENT_MODE getByTag(String tag) {
            for (PAYMENT_MODE mode : PAYMENT_MODE.values()) {
                if (mode.tag.contentEquals(tag))
                    return mode;
            }
            return CASH;
        }
    }

    public ShopModel() {}

    public ShopModel(String pk) {
        this.pk = pk;
    }

    public String getId() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getTagline() {
        return tagline;
    }

    public String getLogoUrl() {
        return logo;
    }

    public String[] getCovers() {
        return covers;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isActive() {
        return active;
    }

    public PAYMENT_MODE[] getPaymentModes() {
        return paymentModes;
    }

    @JsonProperty("payment_mode")
    public String[] serializePaymentModes() {
        String[] modes = new String[paymentModes.length];
        for (int i = 0; i < paymentModes.length; i++) {
            modes[i] = paymentModes[i].tag;
        }
        return modes;
    }

    public boolean isValidStatus() {
        return this.isVerified() && this.isActive();
    }

    public String getShopStatus() {
        String msg = "Shop is properly working.";
        if (!isVerified()) {
            msg = "Shop isn't verified yet.\nContact our support for immediate on-site verification.";
        } else if (!isActive()) {
            msg = "Shop isn't currently serving customers.";
        }
        return msg;
    }

    @JsonProperty("payment_mode")
    public void setPaymentModes(String... paymentModes) {
        this.paymentModes = new PAYMENT_MODE[paymentModes.length];
        int i = 0;
        for (String mode : paymentModes) {
            this.paymentModes[i] = PAYMENT_MODE.getByTag(mode);
            i++;
        }
    }

    public void setPaymentModes(PAYMENT_MODE... paymentModes) {
        this.paymentModes = paymentModes;
    }

    public LocationModel getLocation() {
        return location;
    }

    public String getLocality() {
        return locality;
    }

    public String[] getExtraData() {
        return extraData;
    }

    public void setExtraData(String... extraData) {
        this.extraData = extraData;
    }
}
