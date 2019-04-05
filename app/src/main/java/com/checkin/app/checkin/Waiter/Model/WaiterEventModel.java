package com.checkin.app.checkin.Waiter.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WaiterEventModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("status")
    private CHAT_STATUS_TYPE status;

    @JsonProperty("type")
    private CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("modified")
    private Date modified;

    @JsonProperty("service")
    private EVENT_REQUEST_SERVICE_TYPE service;

    @JsonProperty("ordered_item")
    private SessionOrderedItemModel orderedItem;

    public WaiterEventModel() {
    }

    public static WaiterEventModel fromEventBriefModel(EventBriefModel eventBriefModel) {
        WaiterEventModel result = new WaiterEventModel();
        result.pk = eventBriefModel.getPk();
        result.type = eventBriefModel.getType();
        result.message = eventBriefModel.getMessage();
        result.service = eventBriefModel.getService();
        result.modified = eventBriefModel.getTimestamp();
        return result;
    }

    public long getPk() {
        return pk;
    }

    public CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    public void setStatus(CHAT_STATUS_TYPE status) {
        this.status = status;
    }

    public CHAT_EVENT_TYPE getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = CHAT_EVENT_TYPE.getByTag(type);
    }

    public String getMessage() {
        return message;
    }

    public Date getModified() {
        return modified;
    }

    public EVENT_REQUEST_SERVICE_TYPE getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(int service) {
        this.service = EVENT_REQUEST_SERVICE_TYPE.getByTag(service);
    }

    public SessionOrderedItemModel getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(SessionOrderedItemModel orderedItemModel) {
        this.orderedItem = orderedItemModel;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = CHAT_STATUS_TYPE.getByTag(status);
    }
}
