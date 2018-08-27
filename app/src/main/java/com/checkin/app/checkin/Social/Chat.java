package com.checkin.app.checkin.Social;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

@Entity
public class Chat {
    @Id private long userId;
    @Backlink(to = "chat") private ToMany<Message> messages;
    @Transient private Message lastMessage;

    public Chat(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ToMany<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
