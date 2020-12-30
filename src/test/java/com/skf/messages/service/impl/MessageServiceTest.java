package com.skf.messages.service.impl;

import com.skf.messages.domain.dto.Message;
import com.skf.messages.domain.dto.TimeInterval;
import com.skf.messages.exception.LastNotFoundException;
import com.skf.messages.repository.MessageRepository;
import com.skf.messages.repository.entity.MessageEntity;
import com.skf.messages.service.MessageService;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.skf.messages.repository.entity.MessageEntity.createEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    MessageRepository messageRepository;

    MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageServiceImpl(messageRepository);
    }

    @Test
    void messageShouldBeAdded() {
        String messageContent = "this is message content";
        Message message = messageService.addMessage(messageContent);

        assertNotNull(message);
        assertEquals(messageContent, message.getContent());
        assertTrue(System.currentTimeMillis() >= message.getTimestamp());
    }

    @Test
    void emptyMessageShouldNotBeAdded() {
        assertThrows(NullPointerException.class, () -> messageService.addMessage(null));
        assertThrows(IllegalArgumentException.class, () -> messageService.addMessage(""));
    }

    @Test
    void lastMessageFromEmptyStoreShouldNotBeRetrieved() {
        assertThrows(LastNotFoundException.class, () -> messageService.getLastMessage());
    }

    @Test
    void lastMessageShouldBeRetrieved() {
        String messageContent = "message content";
        Mockito.when(messageRepository.findLast()).thenReturn(Optional.of(createEntity(messageContent)));

        Message message = messageService.getLastMessage();

        assertNotNull(message);
        assertNotNull(message.getContent());
        assertFalse(message.getContent().isEmpty());
        assertTrue(System.currentTimeMillis() >= message.getTimestamp());
    }

    @Test
    void messagesByTimeIntervalShouldBeRetrieved() {
        long from = System.currentTimeMillis();
        List<MessageEntity> entities = Arrays.asList(createEntity("one"), createEntity("two"), createEntity("three"));
        long to = System.currentTimeMillis();
        Mockito.when(messageRepository.findAllByTime(from, to)).thenReturn(entities);
        List<Message> messages = messageService.getMessagesByTimeInterval(new TimeInterval(from, to));
        List<Message> expected = entities.stream().map(Message::new).collect(Collectors.toList());
        assertEquals(expected, messages);
    }

    @Test
    void emptyMessageListShouldBeRetrievedByEmptyTimeInterval() {
        long from = System.currentTimeMillis() - 1000000;
        long to = System.currentTimeMillis();
        List<Message> messages = messageService.getMessagesByTimeInterval(new TimeInterval(from, to));
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }
}