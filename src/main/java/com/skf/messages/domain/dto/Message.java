package com.skf.messages.domain.dto;

import com.skf.messages.repository.entity.MessageEntity;
import lombok.Data;

@Data
public class Message {
    private long timestamp;
    private String content;

    public Message(MessageEntity messageEntity) {
        this.timestamp = messageEntity.getTimestamp();
        this.content = messageEntity.getMessageContent();
    }
}
