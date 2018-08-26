package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class ReviewsItem {
    @Id
    public long id;
    public int img;
    public String name;
    public String followers;
    public String time;
    public String rating;
    public String review;

    public ReviewsItem() {
    }

    public ReviewsItem(int img, String name, String followers, String time, String rating, String review) {
        this.img = img;
        this.name = name;
        this.followers = followers;
        this.time = time;
        this.rating = rating;
        this.review = review;
    }


}
