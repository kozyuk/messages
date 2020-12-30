package com.skf.messages.service;

import com.skf.messages.domain.dto.Message;
import com.skf.messages.domain.dto.TimeInterval;

import java.util.List;

public interface MessageService {

    Message addMessage(String message);

    Message getLastMessage();

    List<Message> getMessagesByTimeInterval(TimeInterval timeInterval);
}
