package com.skf.messages.service.impl;

import com.skf.messages.domain.dto.Message;
import com.skf.messages.domain.dto.TimeInterval;
import com.skf.messages.exception.LastNotFoundException;
import com.skf.messages.repository.MessageRepository;
import com.skf.messages.repository.entity.MessageEntity;
import com.skf.messages.service.MessageService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.skf.messages.domain.Messages.fromMessageEntities;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message addMessage(String content) {
        MessageEntity entity = MessageEntity.createEntity(content);
        messageRepository.save(entity);
        return new Message(entity);
    }

    @Override
    public Message getLastMessage() {
        MessageEntity entity = messageRepository.findLast()
                .orElseThrow(() -> new LastNotFoundException("Can't find any message"));
        return new Message(entity);
    }

    @Override
    public List<Message> getMessagesByTimeInterval(TimeInterval timeInterval) {
        List<MessageEntity> messageEntries = messageRepository.findAllByTime(timeInterval.getFrom(), timeInterval.getTo());
        return fromMessageEntities(messageEntries).toList();
    }
}
