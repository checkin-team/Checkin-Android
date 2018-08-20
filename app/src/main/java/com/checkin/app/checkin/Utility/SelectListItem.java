package com.checkin.app.checkin.Utility;

public class SelectListItem {
    private String imageUrl;
    private String title;
    private String desc;
    private Object data;

    public SelectListItem(String imageUrl, String title, String  desc, Object data) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}