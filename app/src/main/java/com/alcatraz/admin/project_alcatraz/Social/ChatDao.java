package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT message AS lastMessage, " +
            "MAX(sent_at) AS sentAt, chat.user_id AS userId, " +
            "sender_id AS senderId, user.username AS userName " +
            "FROM chats AS chat LEFT JOIN messages AS msg " +
            "ON chat.user_id = msg.chat_id " +
            "INNER JOIN users AS user ON user.id = user_id " +
            "GROUP BY chat_id ORDER BY sentAt DESC")
    LiveData<List<BriefChat>> getRecentBriefChats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Chat... chats);

    @Query("SELECT * FROM chats WHERE user_id = :userId")
    Chat getById(int userId);

    class BriefChat {
        public int userId, senderId;
        public String userName, lastMessage;
        public Date sentAt;

        public BriefChat(int userId, int senderId, String lastMessage, String userName, Date sentAt) {
            this.lastMessage = lastMessage;
            this.senderId = senderId;
            this.userName = userName;
            this.sentAt = sentAt;
            this.userId = userId;
        }
    }
}
