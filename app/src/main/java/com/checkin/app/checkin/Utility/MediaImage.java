package com.checkin.app.checkin.Utility;

/**
 * Created by Jogi Miglani on 07-09-2018.
 */

public class MediaImage {
    private String mTitle, mPath;

    MediaImage(String imageTitle, String imagePath) {
        mTitle = imageTitle;
        mPath = imagePath;
    }

    public String getPath() {
        return mPath;
    }

    public String getTitle() {
        return mTitle;
    }
}
