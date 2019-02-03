package com.checkin.app.checkin.Notifications;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserNotificationModel {
    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("count_requests")
    private long countRequests;

    @JsonProperty("notifications")
    private List<NotificationItemModel> notifications;

    public UserNotificationModel() {}

    public BriefModel getUser() {
        return user;
    }

    public long getCountRequests() {
        return countRequests;
    }

    public List<NotificationItemModel> getNotifications() {
        return notifications;
    }
}
