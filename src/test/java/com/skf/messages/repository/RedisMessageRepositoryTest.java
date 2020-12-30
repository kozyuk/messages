package com.skf.messages.repository;

import com.skf.messages.repository.entity.MessageEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class RedisMessageRepositoryTest {

    @Container
    static GenericContainer redis = new GenericContainer<>("redis:6.0.9-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.redis.host", redis::getHost);
    }

    private static final String REDIS_MESSAGES_KEY = "Messages";

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    RedisTemplate<String, MessageEntity> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(REDIS_MESSAGES_KEY);
    }

    @Test
    void messageEntityShouldBePersisted() {
        MessageEntity entity = MessageEntity.createEntity("content");
        messageRepository.save(entity);

        assertTrue(redisTemplate.keys(REDIS_MESSAGES_KEY).contains(REDIS_MESSAGES_KEY));
        assertEquals(1, redisTemplate.opsForZSet().zCard(REDIS_MESSAGES_KEY));

        MessageEntity actual = redisTemplate.opsForZSet().range(REDIS_MESSAGES_KEY, 0, 1).stream().findFirst().get();
        assertEquals(entity.getUuid(), actual.getUuid());
        assertEquals(entity.getTimestamp(), actual.getTimestamp());
        assertEquals(entity.getMessageContent(), actual.getMessageContent());
    }

    @Test
    void messageEntityWithExistContentShouldBePersisted() {
        int messageCount = 10000;
        List<MessageEntity> expected = new ArrayList<>();
        for (int i = 0; i < messageCount; i++) {
            MessageEntity entity = MessageEntity.createEntity("content");
            messageRepository.save(entity);
            expected.add(entity);
        }

        assertTrue(redisTemplate.keys(REDIS_MESSAGES_KEY).contains(REDIS_MESSAGES_KEY));
        assertEquals(messageCount, redisTemplate.opsForZSet().zCard(REDIS_MESSAGES_KEY));

    }

    @Test
    void existMessageEntityShouldNotBePersisted() {
        MessageEntity entity = MessageEntity.createEntity("content");
        messageRepository.save(entity);
        messageRepository.save(entity);
        messageRepository.save(entity);

        assertTrue(redisTemplate.keys(REDIS_MESSAGES_KEY).contains(REDIS_MESSAGES_KEY));
        assertEquals(1, redisTemplate.opsForZSet().zCard(REDIS_MESSAGES_KEY));

        List<MessageEntity> actual = redisTemplate.opsForZSet().range(REDIS_MESSAGES_KEY, 0, 10).stream().collect(toList());
        assertEquals(1, actual.size());
    }

    @Test
    void lastMessageShouldBeRetrieved() {
        MessageEntity entity = MessageEntity.createEntity("content 1");
        redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());
        entity = MessageEntity.createEntity("content 2");
        redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());
        entity = MessageEntity.createEntity("content 3");
        redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());

        MessageEntity actual = messageRepository.findLast().get();

        assertEquals(entity.getUuid(), actual.getUuid());
        assertEquals(entity.getTimestamp(), actual.getTimestamp());
        assertEquals(entity.getMessageContent(), actual.getMessageContent());
    }

    @Test
    void lastMessageShouldBeNotRetrievedIfDBIsEmpty() {
        Optional<MessageEntity> actual = messageRepository.findLast();
        assertFalse(actual.isPresent());
    }


    @Test
    void allMessagesByTimeIntervalShouldBeRetrieved() {
        int messageCount = 10000;
        for (int i = 0; i < messageCount; i++) {
            MessageEntity entity = MessageEntity.createEntity("content " + i);
            redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());
        }
        List<MessageEntity> actual = messageRepository.findAllByTime(Long.MIN_VALUE, Long.MAX_VALUE);
        assertEquals(messageCount, actual.size());
    }

    @Test
    void allMessagesByTimeIntervalShouldBeSorted() {
        int messageCount = 10000;
        long timestamp = System.currentTimeMillis();
        int delta = 3_600_000;
        for (int i = 0; i < messageCount; i++) {
            MessageEntity entity = MessageEntity.createEntity(timestamp, "timestamp: " + timestamp);
            redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());
            timestamp = timestamp - delta;
        }
        List<MessageEntity> actual = messageRepository.findAllByTime(Long.MIN_VALUE, Long.MAX_VALUE);
        assertEquals(messageCount, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            timestamp = timestamp + delta;
            MessageEntity entity = actual.get(i);
            assertEquals(timestamp, entity.getTimestamp());
            assertEquals("timestamp: " + timestamp, entity.getMessageContent());
        }
    }

    @Test
    void onlyMessagesByTimeIntervalShouldBeRetrieved() {
        int messageCount = 10000;
        long timestamp = System.currentTimeMillis();
        int delta = 3_600_000;
        long from = timestamp - delta * 6500;
        long to = timestamp - delta * 3500;
        List<MessageEntity> expected = new ArrayList<>();
        MessageEntity entity = null;
        for (int i = 0; i < messageCount; i++) {
            entity = MessageEntity.createEntity(timestamp, "timestamp: " + timestamp);
            redisTemplate.opsForZSet().add(REDIS_MESSAGES_KEY, entity, entity.getTimestamp());
            timestamp = timestamp - delta;
            if (entity.getTimestamp() > from && entity.getTimestamp() <= to) {
                expected.add(entity);
            }
        }
        Collections.reverse(expected);
        List<MessageEntity> actual = messageRepository.findAllByTime(from, to);

        assertEquals(expected, actual);

        from = timestamp + delta;
        to = from + 1;

        actual = messageRepository.findAllByTime(from, to);
        assertEquals(1, actual.size());
        assertEquals(entity, actual.get(0));
    }

    @Test
    void messagesByTimeIntervalShouldBeNotRetrievedIfDBIsEmpty() {
        List<MessageEntity> actual = messageRepository.findAllByTime(Long.MIN_VALUE, Long.MAX_VALUE);
        assertTrue(actual.isEmpty());
    }
}