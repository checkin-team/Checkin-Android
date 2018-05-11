package com.alcatraz.admin.project_alcatraz.Session;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by shivanshs9 on 7/5/18.
 */

public class MenuGroup {
    protected String title;
    protected ArrayList<MenuItem> items;
    protected Bitmap image;

    public MenuGroup(@NonNull final String title, final ArrayList<MenuItem> items, final Bitmap image) {
        this.title = title;
        this.items = items;
        this.image = image;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
