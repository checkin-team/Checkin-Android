package com.checkin.app.checkin.Profile.ShopProfile;

import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Shop.ShopModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

@Entity
public class ReviewsItem {
    @Id
    public long id;
    public ToOne<ShopModel> shop;
    public int img;
    public String name;
    public String followers;
    public String time;
    public String rating;
    public String review;

    public ReviewsItem(){}
    public ReviewsItem(int img, String name, String followers, String time, String rating, String review,long target) {
        this.img = img;
        this.name = name;
        shop.setTargetId(target);
        this.followers = followers;
        this.time = time;
        this.rating = rating;
        this.review = review;
    }


}
