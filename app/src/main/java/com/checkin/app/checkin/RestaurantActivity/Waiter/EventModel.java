package com.checkin.app.checkin.RestaurantActivity.Waiter;


import com.checkin.app.checkin.Menu.OrderedItemModel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EventModel {
    @JsonProperty("pk")
//    @Id(assignable = true)
    private long id;

    private STATUS status;
    private TYPE type;


    @JsonProperty("order_item")
    private OrderedItemModel orderedItem;

    @JsonProperty("message")
    private String customMessage;

    @JsonProperty("time")
    private Date time;

    public enum STATUS {
        INCOMPLETE(0), COMPLETE(1);

        final int tag;
        STATUS(int tag) {
            this.tag = tag;
        }//constructor of enum

        public static STATUS getByTag(int tag) {
            switch (tag) {
                case '1':
                    return COMPLETE;
                case '0':
                    return INCOMPLETE;
            }
            return INCOMPLETE;
        }
    }


    public enum TYPE {
        CUSTOM_MESSAGE(501), ORDERED_ITEM(502),
        REQUEST_FOR_WATER(503),REQUEST_FOR_BILL(504)
        ,REQUEST_FOR_TABLE_CLEAN(505), REQUEST_FOR_CALL_WAITER(506);

        final int tag;
        TYPE(int tag) {
            this.tag = tag;
        }//constructor of enum

        public static TYPE getByTag(int tag) {
            switch (tag) {
                case 501:
                    return CUSTOM_MESSAGE;
                case 502:
                    return ORDERED_ITEM;
                case 503:
                    return REQUEST_FOR_WATER;
                case 504:
                    return REQUEST_FOR_BILL;
                case 505:
                    return REQUEST_FOR_TABLE_CLEAN;
                case 506:
                    return REQUEST_FOR_CALL_WAITER;
            }
            return CUSTOM_MESSAGE;
        }
    }

    @JsonProperty("type")
    public void setType(int tag) {
        this.type = TYPE.getByTag(tag);
    }

    public TYPE getType() {
        return type;
    }

    @JsonProperty("status")
    public void setStatus(int tag) {
        this.status=STATUS.getByTag(tag);
    }

    public STATUS getStatus() {
        return status;
    }

    //Status ka Enum
    //Type ka Enum


    public EventModel()
    {

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public OrderedItemModel getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(OrderedItemModel orderedItem) {
        this.orderedItem = orderedItem;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

}
