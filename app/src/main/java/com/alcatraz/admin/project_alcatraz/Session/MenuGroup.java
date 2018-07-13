package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivanshs9 on 7/5/18.
 */

@Entity(tableName = "menu_groups")
public class MenuGroup {
    @PrimaryKey(autoGenerate = true) private int id;
    private String title;
    @SerializedName("sub_groups") @ColumnInfo(name = "sub_groups") private ArrayList<String> subGroups;
    @ColumnInfo(name = "image_url") private String mImageUrl;
    @ColumnInfo(name = "menu_id") private int menuId;
    @Ignore private List<MenuItem> mItems;

    public MenuGroup(@NonNull final String title, final ArrayList<String> subGroups, int menuId) {
        this.title = title;
        if (subGroups != null) {
            this.subGroups = subGroups;
        } else {
            this.subGroups = new ArrayList<>(1);
            this.subGroups.add("Default");
        }
        this.menuId = menuId;
        this.mItems = new ArrayList<>();
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getSubGroupsCount() {
        return subGroups != null ? subGroups.size() : 0;
    }

    public ArrayList<String> getSubGroups() {
        return subGroups;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public List<MenuItem> getItems() {
        return mItems;
    }

    public void setItems(List<MenuItem> items) {
        this.mItems = items;
    }

    public void addItem(MenuItem item) {
        mItems.add(item);
    }
}
