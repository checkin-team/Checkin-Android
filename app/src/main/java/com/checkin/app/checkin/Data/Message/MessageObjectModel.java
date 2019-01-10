package com.checkin.app.checkin.Data.Message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageObjectModel {
    private MESSAGE_OBJECT_TYPE type;

    @JsonProperty("text")
    private String text;

    @JsonProperty("pk")
    private int pk;

    @JsonProperty("img_url")
    private String imgUrl;

    public enum MESSAGE_OBJECT_TYPE {
        NONE(0), USER(1), SESSION(2), REVIEW(3), FRIENDSHIP_REQUEST(4),
        RESTAURANT(5);

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

    public String getText() {
        return text;
    }

    public int getPk() {
        return pk;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = MESSAGE_OBJECT_TYPE.getById(type);
    }
}
