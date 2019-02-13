package com.checkin.app.checkin.ImageGallery;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGalleryModel {
    @JsonProperty("title")
    private String title;

    @JsonProperty("uploader")
    private BriefModel uploader;

    @JsonProperty("images")
    private List<String> images = null;

    public ImageGalleryModel() {}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUploader(BriefModel uploader) {
        this.uploader = uploader;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public BriefModel getUploader() {
        return uploader;
    }

    public List<String> getImages() {
        return images;
    }
}
