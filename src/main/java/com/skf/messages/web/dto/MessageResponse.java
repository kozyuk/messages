package com.skf.messages.web.dto;

import com.skf.messages.domain.dto.Message;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MessageResponse {

    private String content;

    public static MessageResponse create(Message message) {
        return new MessageResponse(message.getContent());
    }
}
