package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MenuDao {
    @Insert(onConflict = REPLACE)
    void insertItems(MenuItem... menuItems);

    @Insert(onConflict = REPLACE)
    void insertGroups(MenuGroup... menuGroups);

    @Query("SELECT * FROM menu_items AS item WHERE item.menu_id = :menuId")
    LiveData<List<MenuItem>> getMenuItems(int menuId);

    @Query("SELECT * FROM menu_groups AS grp WHERE grp.menu_id = :menuId")
    LiveData<List<MenuGroup>> getMenuGroups(int menuId);
}
