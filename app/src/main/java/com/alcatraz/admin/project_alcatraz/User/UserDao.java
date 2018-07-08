package com.alcatraz.admin.project_alcatraz.User;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY id ASC")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    List<User> filterByIds(int... userIds);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getById(int userId);

    @Insert(onConflict = REPLACE)
    void insertAll(User... users);

    @Delete
    void deleteAll(User... users);
}
