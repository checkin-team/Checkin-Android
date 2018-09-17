package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.NamedFormatter;

import java.util.HashMap;
import java.util.Map;

public class ShopModel {
    private int mId;
    private String mName, mCategory, mImageUrl;

    public ShopModel(int id, String name, String category) {
        mId = id;
        mName = name;
        mCategory = category;
    }

    public ShopModel(int id, String name, String category, String imageUrl) {
        mId = id;
        mName = name;
        mCategory = category;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getProfileUrl() {
        Map<String, String> values = new HashMap<>();
        values.put(Constants.SHOP_ID, "" + mId);
        return NamedFormatter.format(Constants.API_URL_SHOP_PROFILE_URL, values);
    }
}
