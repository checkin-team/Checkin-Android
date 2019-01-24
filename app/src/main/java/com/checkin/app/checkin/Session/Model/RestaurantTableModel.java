package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("event")
    private EventBriefModel event;

    @JsonProperty("created")
    private Date created;

    public RestaurantTableModel() {
    }

    public long getPk() {
        return pk;
    }

    public String getTable() {
        return table;
    }

    public BriefModel getHost() {
        return host;
    }

    public EventBriefModel getEvent() {
        return event;
    }

    public Date getCreated() {
        return created;
    }

    public static String getFormattedTimeFromDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm",Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}