package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "chats", indices = {@Index("user_id")})
public class Chat {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int userId;

    public Chat(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
