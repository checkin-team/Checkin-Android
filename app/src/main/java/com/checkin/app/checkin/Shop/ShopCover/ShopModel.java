package com.checkin.app.checkin.Shop.ShopCover;

import android.media.Image;

public class ShopModel {

    public int getSrNo() {
        return SrNo;
    }

    public int getImage() {
        return image;
    }

    private int SrNo;
    private int image;

    public ShopModel(int srNo, int image) {
        SrNo = srNo;
        this.image = image;
    }
}
