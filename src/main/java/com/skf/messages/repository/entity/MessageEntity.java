package com.skf.messages.repository.entity;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class MessageEntity {

    private String uuid;
    private long timestamp;
    @NonNull
    private String messageContent;

    public static MessageEntity createEntity(String content) {
        long timestamp = System.currentTimeMillis();
        return createEntity(timestamp, content);
    }

    public static MessageEntity createEntity(long timestamp, String content) {
        if (content.isEmpty())
            throw new IllegalArgumentException("Content can't be empty");
        String id = UUID.randomUUID().toString();
        return new MessageEntity(id, timestamp, content);
    }
}
