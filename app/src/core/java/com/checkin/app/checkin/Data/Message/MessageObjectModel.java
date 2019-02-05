package com.checkin.app.checkin.Data.Message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MessageObjectModel implements Serializable {
    private MESSAGE_OBJECT_TYPE type;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("pk")
    private int pk;

    @JsonProperty("display_pic_url")
    private String displayPicUrl;

    public enum MESSAGE_OBJECT_TYPE {
        NONE(0), USER(1), SESSION(2), REVIEW(3), FRIENDSHIP_REQUEST(4),
        RESTAURANT(5), ORDER_ITEM(6), SESSION_EVENT(7), RESTAURANT_CUSTOMER(8),
        RESTAURANT_MEMBER(9);

        int id;
        MESSAGE_OBJECT_TYPE(int id) {
            this.id = id;
        }

        static MESSAGE_OBJECT_TYPE getById(int id) {
            for (MESSAGE_OBJECT_TYPE type: MESSAGE_OBJECT_TYPE.values()) {
                if (type.id == id)
                    return type;
            }
            return NONE;
        }
    }

    public MessageObjectModel() {}

    public MESSAGE_OBJECT_TYPE getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPk() {
        return pk;
    }

    public String getDisplayPicUrl() {
        return displayPicUrl;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = MESSAGE_OBJECT_TYPE.getById(type);
    }
}
