package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY id ASC")
    LiveData<List<Message>> getAll();

    @Query("SELECT * FROM messages WHERE id IN (:messageIds)")
    List<Message> filterByIds(int... messageIds);

    @Insert(onConflict = REPLACE)
    void insertAll(Message... messages);

    @Update
    void updateAll(Message... messages);

    @Delete
    void deleteAll(Message... message);

    @Query("SELECT * FROM messages WHERE sender_id IN (:senderIds)")
    LiveData<List<Message>> getMessagesFrom(int... senderIds);

    @Query("SELECT * FROM messages WHERE recipient_id IN (:recipientIds)")
    LiveData<List<Message>> getMessagesTo(int... recipientIds);

    @Query("SELECT * FROM messages AS msg WHERE msg.chat_id = :chatId")
    LiveData<List<Message>> getMessagesOfChat(int chatId);
}
