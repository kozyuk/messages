package com.skf.messages.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class MessageRequest {
    @NonNull
    private String content;
}
