package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.User.UserModel;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

public class ShopReview {
    private long id;
    private UserModel user;
    private Date time;
    private float rating;
    private String review;

    public ShopReview() {}

    public String getUserName() {
        return user.getUsername();
    }

    public String getUserProfilePic() {
        return user.getProfilePic();
    }

    public Date getTime() {
        return time;
    }

    public float getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}
