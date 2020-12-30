package com.skf.messages.domain;

import com.skf.messages.repository.entity.MessageEntity;
import com.skf.messages.domain.dto.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Messages {

    private List<Message> messages;

    private Messages(List<Message> messages) {
        this.messages = messages;
    }

    public static Messages fromMessageEntities(MessageEntity... messageEntities) {
        return fromMessageEntities(Arrays.asList(messageEntities));
    }

    public static Messages fromMessageEntities(List<MessageEntity> messageEntities) {
        return new Messages(messageEntities.stream().map(Message::new).collect(Collectors.toList()));
    }

    public List<Message> toList() {
        return messages;
    }
}
