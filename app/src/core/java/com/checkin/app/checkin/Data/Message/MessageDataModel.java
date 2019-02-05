package com.checkin.app.checkin.Data.Message;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDataModel {
    @JsonProperty("session__bill__total")
    private double sessionBillTotal;

    @JsonProperty("user__gender")
    private UserModel.GENDER userGender;

    @JsonProperty("session__bill__payment_mode")
    private ShopModel.PAYMENT_MODE sessionBillPaymentMode;

    @JsonProperty("session__event_status")
    private SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus;

    @JsonProperty("session__customer_count")
    private int sessionCustomerCount;

    @JsonDeserialize(using = SessionOrderedItemModel.SessionOrderedItemDeserializer.class)
    @JsonProperty("session__ordered_item")
    private SessionOrderedItemModel sessionOrderedItem;

    @JsonDeserialize(using = SessionChatModel.SessionChatDeserializer.class)
    @JsonProperty("session__event_detail")
    private SessionChatModel sessionEventDetail;

    @JsonDeserialize(using = EventBriefModel.EventBriefModelDeserializer.class)
    @JsonProperty("session__event")
    private EventBriefModel sessionEventBrief;

    @JsonProperty("session__table")
    private String sessionTableName;

    public MessageDataModel() {
    }

    @JsonProperty("user__gender")
    public void setUserGender(char gender) {
        this.userGender = UserModel.GENDER.getByTag(gender);
    }

    @JsonProperty("session__bill__payment_mode")
    public void setSessionBillPaymentMode(String sessionBillPaymentMode) {
        this.sessionBillPaymentMode = ShopModel.PAYMENT_MODE.getByTag(sessionBillPaymentMode);
    }

    @JsonProperty("session__event_status")
    public void setSessionEventStatus(int status) {
        this.sessionEventStatus = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
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

    public SessionChatModel getSessionEventDetail() {
        return sessionEventDetail;
    }

    public SessionChatModel.CHAT_STATUS_TYPE getSessionEventStatus() {
        return sessionEventStatus;
    }

    public EventBriefModel getSessionEventBrief() {
        return sessionEventBrief;
    }

    public String getSessionTableName() {
        return sessionTableName;
    }

    public static class MessageDataDeserializer extends JsonDeserializer<MessageDataModel> {
        @Override
        public MessageDataModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return Converters.objectMapper.readValue(jsonParser.getText(), MessageDataModel.class);
        }
    }
}
