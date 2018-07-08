package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.text.format.DateFormat;


import com.alcatraz.admin.project_alcatraz.Data.AppRoomDatabase;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by TAIYAB on 14-06-2018.
 */

@Entity(tableName = "messages",
        indices = {@Index("chat_id"), @Index("read_at"), @Index("recipient_id"), @Index("sender_id")},
        foreignKeys = {
            @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "sender_id"),
            @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "recipient_id"),
            @ForeignKey(entity = Chat.class, parentColumns = "user_id", childColumns = "chat_id")
})
public class Message {

    @PrimaryKey(autoGenerate = true) private int id;
    @SerializedName(value = "body") private String message;
    @SerializedName(value = "sender") @ColumnInfo(name = "sender_id") private int senderId;
    @SerializedName(value = "recipient") @ColumnInfo(name = "recipient_id") private int recipientId;
    @SerializedName(value = "read_at") @ColumnInfo(name = "read_at") private Date readAt;
    @SerializedName(value = "sent_at") @ColumnInfo(name = "sent_at") private Date sentAt;
    @ColumnInfo(name = "chat_id") private int chatId;
    @Ignore private final String timeFormat = "hh:mm a";

    public Message(String message, Date sentAt, int senderId, int recipientId) {
        this.message = message;
        this.sentAt = sentAt;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.chatId = (recipientId == 0 ? senderId : recipientId);
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public Date getReadAt() {
        return readAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public String getTime() {
        return DateFormat.format(timeFormat, sentAt).toString();
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
