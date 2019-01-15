package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveSessionCustomChatDataModel {
    @JsonProperty("user")
    private int user;

    @JsonProperty("ordered_item")
    private int ordered_item;

    private CHATSERVICETYPES service;

    @JsonProperty("canceler")
    private ActiveSessionChatCancelerModel canceler;

    public enum CHATSERVICETYPES {
        SERVICE_NONE(0), SERVICE_CALL_WAITER(1), SERVICE_CLEAN_TABLE(2), SERVICE_BRING_COMMODITY(3);

        final int tag;

        CHATSERVICETYPES(int tag) {
            this.tag = tag;
        }

        public static CHATSERVICETYPES getByTag(int tag) {
            switch (tag) {
                case 0:
                    return SERVICE_NONE;
                case 1:
                    return SERVICE_CALL_WAITER;
                case 2:
                    return SERVICE_CLEAN_TABLE;
                case 3:
                    return SERVICE_BRING_COMMODITY;
            }
            return SERVICE_NONE;
        }
    }

    public ActiveSessionCustomChatDataModel(){}

    public ActiveSessionCustomChatDataModel(int user, int ordered_item, CHATSERVICETYPES service, ActiveSessionChatCancelerModel canceler) {
        this.user = user;
        this.ordered_item = ordered_item;
        this.service = service;
        this.canceler = canceler;
    }

    public int getUser() {
        return user;
    }

    public int getOrdered_item() {
        return ordered_item;
    }

    public ActiveSessionChatCancelerModel getCanceler() {
        return canceler;
    }

    @JsonProperty("service")
    public void setType(int tag) {
        this.service = CHATSERVICETYPES.getByTag(tag);
    }

    public CHATSERVICETYPES getService() {
        return service;
    }
}
