package com.alcatraz.admin.project_alcatraz.Social;

import android.text.format.DateFormat;

import com.alcatraz.admin.project_alcatraz.User.User;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

/**
 * Created by TAIYAB on 14-06-2018.
 */

@Entity
public class Message {

    @Id private long id;
    @SerializedName(value = "body") private String message;
    @SerializedName(value = "sender") private ToOne<User> sender;
    @SerializedName(value = "recipient") private ToOne<User> recipient;
    @SerializedName(value = "read_at") private Date readAt;
    @SerializedName(value = "sent_at") private Date sentAt;
    private ToOne<Chat> chat;
    @Transient private static final String timeFormat = "hh:mm a";

    public Message(String message, Date sentAt, long senderId, long recipientId) {
        this.message = message;
        this.sentAt = sentAt;
        this.sender.setTargetId(senderId);
        this.recipient.setTargetId(recipientId);
        this.chat.setTargetId(recipientId == 0 ? senderId : recipientId);
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
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

    public long getSenderId() {
        return sender.getTargetId();
    }

    public long getRecipientId() {
        return recipient.getTargetId();
    }

    public ToOne<Chat> getChat() {
        return chat;
    }

    public ToOne<User> getRecipient() {
        return recipient;
    }

    public ToOne<User> getSender() {
        return sender;
    }

    public long getChatId() {
        return chat.getTargetId();
    }

}
