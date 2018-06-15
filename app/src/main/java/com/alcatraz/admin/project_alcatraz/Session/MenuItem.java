package com.alcatraz.admin.project_alcatraz.Session;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class MenuItem {
    protected String title;
    protected float[] costs;
    protected String[] types;
    protected Bitmap image;
    protected MediaStore.Video video;
    protected ArrayList<MenuItem> items;

    MenuItem(String title, String[] types, float[] costs)
    {
        this.title = title;
        this.types = types;
        this.costs = costs;
    }
}

