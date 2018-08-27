package com.checkin.app.checkin.Social;

import android.text.format.DateFormat;

import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty(value = "body") private String message;
    @JsonProperty(value = "sender") private ToOne<UserModel> sender;
    @JsonProperty(value = "recipient") private ToOne<UserModel> recipient;
    @JsonProperty(value = "read_at") private Date readAt;
    @JsonProperty(value = "sent_at") private Date sentAt;
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

    public ToOne<UserModel> getRecipient() {
        return recipient;
    }

    public ToOne<UserModel> getSender() {
        return sender;
    }

    public long getChatId() {
        return chat.getTargetId();
    }

}
