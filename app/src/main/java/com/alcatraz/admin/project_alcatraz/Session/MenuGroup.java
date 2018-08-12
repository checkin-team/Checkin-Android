package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.Converters;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

/**
 * Created by shivanshs9 on 7/5/18.
 */

@Entity
public class MenuGroup {
    @Id private long id;
    private String name;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @SerializedName("sub_groups")
    private ArrayList<String> subGroups;
    @Transient private String mImageUrl;
    private int menuId;
    @Backlink(to = "group")
    private ToMany<MenuItem> items;

    MenuGroup() {
        this.subGroups = new ArrayList<>(1);
        this.subGroups.add("Default");
    }

    public MenuGroup(@NonNull final String name, final ArrayList<String> subGroups, int menuId) {
        this.name = name;
        if (subGroups != null) {
            this.subGroups = subGroups;
        } else {
            this.subGroups = new ArrayList<>(1);
            this.subGroups.add("Default");
        }
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public int getSubGroupsCount() {
        return subGroups != null ? subGroups.size() : 0;
    }

    public ArrayList<String> getSubGroups() {
        return subGroups;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getMenuId() {
        return menuId;
    }

    public ToMany<MenuItem> getItems() {
        return items;
    }
}
