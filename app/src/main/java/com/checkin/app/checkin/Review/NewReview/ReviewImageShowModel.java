package com.checkin.app.checkin.Review.NewReview;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class ReviewImageShowModel {

    private String pk;

    private File image;

    public ReviewImageShowModel() {
    }

    public ReviewImageShowModel(String pk, File image) {
        this.pk = pk;
        this.image = image;
    }

    public String getPk() {
        return pk;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}
