package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

/**
 * Created by ACER on 1/2/2019.
 */

public class ActiveSessionChatTwoModel {
    private int id;
    private String message;
    private String time;

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public ActiveSessionChatTwoModel(int id, String message, String time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }




}
