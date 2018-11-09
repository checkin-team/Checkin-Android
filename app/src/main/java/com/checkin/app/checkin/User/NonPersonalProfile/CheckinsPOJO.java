package com.checkin.app.checkin.User.NonPersonalProfile;

// Created by viBHU on 17-10-2018

import com.bumptech.glide.Glide;

public class CheckinsPOJO {
    private String mResturantName;
    private String mUserCheckins;
    private Glide mResturantPic;


    public CheckinsPOJO() {}

    public CheckinsPOJO(String resturantName, String userCheckins/*,Glide resturantPic */){
        this.mResturantName = resturantName;
        this.mUserCheckins = userCheckins;
        /*this.mResturantPic = resturantPic;*/
    }

    public String getmResturantName() {
        return mResturantName;
    }

    public String getmUserCheckins() {
        return mUserCheckins;
    }

    public void setmResturantName(String resturantName) {
        this.mResturantName = resturantName;
    }

    public void setmUserCheckins(String mUserCheckins) {
        this.mUserCheckins = mUserCheckins;
    }
}
