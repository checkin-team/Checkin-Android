package com.checkin.app.checkin.Data.Message;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDataModel implements Serializable {
    @JsonProperty("session__bill__total")
    private double sessionBillTotal;

    @JsonProperty("user__gender")
    private char userGender;

    @JsonProperty("session__bill__payment_mode")
    private String sessionBillPaymentMode;

    @JsonProperty("session__event__status")
    private int sessionEventStatus;

    @JsonProperty("session__customer_count")
    private int sessionCustomerCount;

    @JsonProperty("session__table")
    private String sessionTableName;

    @JsonProperty("session__order_id")
    private long sessionOrderId;

    @JsonProperty("session__ordered_item")
    private SessionOrderedItemModel sessionOrderedItem;

    @JsonProperty("session__event_detail")
    private SessionChatModel sessionEventDetail;

    @JsonProperty("session__event")
    private ManagerSessionEventModel sessionEventBrief;

    @JsonProperty("session__qr_id")
    private long sessionQRId;

    @JsonCreator
    public MessageDataModel() {
    }

    public double getSessionBillTotal() {
        return sessionBillTotal;
    }

    public UserModel.GENDER getUserGender() {
        return UserModel.GENDER.getByTag(userGender);
    }

    public ShopModel.PAYMENT_MODE getSessionBillPaymentMode() {
        return ShopModel.PAYMENT_MODE.getByTag(sessionBillPaymentMode);
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
        return SessionChatModel.CHAT_STATUS_TYPE.getByTag(sessionEventStatus);
    }

    public ManagerSessionEventModel getSessionEventBrief() {
        return sessionEventBrief;
    }

    public String getSessionTableName() {
        return sessionTableName;
    }

    public long getSessionOrderId() {
        return sessionOrderId;
    }

    public long getSessionQRId() {
        return sessionQRId;
    }

    public static class MessageDataDeserializer extends JsonDeserializer<MessageDataModel> {
        @Override
        public MessageDataModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return Converters.objectMapper.readValue(jsonParser.getText(), MessageDataModel.class);
        }
    }
}
