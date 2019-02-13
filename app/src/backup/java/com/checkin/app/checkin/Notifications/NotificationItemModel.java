package com.checkin.app.checkin.Notifications;

import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;

public class NotificationItemModel {
    @JsonProperty("actor")
    private MessageObjectModel actor;

    @JsonProperty("description")
    private String description;

    @JsonProperty("object")
    private MessageObjectModel object;

    @JsonProperty("target")
    private MessageObjectModel target;

    @JsonProperty("data")
    private ObjectNode data;

    @JsonProperty("modified")
    private Date modified;

    @JsonProperty("type")
    private MESSAGE_TYPE type;

    public NotificationItemModel() {}

    public MessageObjectModel getActor() {
        return actor;
    }

    public String getDescription() {
        return description;
    }

    public MessageObjectModel getObject() {
        return object;
    }

    public MessageObjectModel getTarget() {
        return target;
    }

    public ObjectNode getData() {
        return data;
    }

    public Date getModified() {
        return modified;
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = MESSAGE_TYPE.getById(type);
    }
}
