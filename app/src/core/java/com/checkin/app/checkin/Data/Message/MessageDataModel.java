package com.checkin.app.checkin.Data.Message;

import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDataModel {
    @JsonProperty("session__bill__total")
    private double sessionBillTotal;

    @JsonProperty("user__gender")
    private UserModel.GENDER userGender;

    @JsonProperty("session__bill__payment_mode")
    private ShopModel.PAYMENT_MODE sessionBillPaymentMode;

    @JsonProperty("session__customer_count")
    private int sessionCustomerCount;

    @JsonProperty("session__ordered_item")
    private SessionOrderedItemModel sessionOrderedItem;

    public MessageDataModel() {
    }

    @JsonProperty("user__gender")
    private void setUserGender(char gender) {
        this.userGender = UserModel.GENDER.getByTag(gender);
    }

    @JsonProperty("session__bill__payment_mode")
    public void setSessionBillPaymentMode(String sessionBillPaymentMode) {
        this.sessionBillPaymentMode = ShopModel.PAYMENT_MODE.getByTag(sessionBillPaymentMode);
    }

    public double getSessionBillTotal() {
        return sessionBillTotal;
    }

    public UserModel.GENDER getUserGender() {
        return userGender;
    }

    public ShopModel.PAYMENT_MODE getSessionBillPaymentMode() {
        return sessionBillPaymentMode;
    }

    public int getSessionCustomerCount() {
        return sessionCustomerCount;
    }

    public SessionOrderedItemModel getSessionOrderedItem() {
        return sessionOrderedItem;
    }
}
