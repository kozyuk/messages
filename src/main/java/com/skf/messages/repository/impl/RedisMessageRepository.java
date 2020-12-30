package com.skf.messages.repository.impl;

import com.skf.messages.exception.LastNotFoundException;
import com.skf.messages.repository.MessageRepository;
import com.skf.messages.repository.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class RedisMessageRepository implements MessageRepository {

    private static final String MESSAGES_KEY = "Messages";

    @Autowired
    private RedisTemplate<String, MessageEntity> redisTemplate;

    @Override
    public void save(MessageEntity entity) {
        double score = entity.getTimestamp();
        redisTemplate.opsForZSet().add(MESSAGES_KEY, entity, score);
    }

    @Override
    public Optional<MessageEntity> findLast() {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(MESSAGES_KEY, 0, 1)
                .stream()
                .findFirst()
                .map(messageEntityTypedTuple -> Optional.of(messageEntityTypedTuple.getValue()))
                .orElse(Optional.empty());
    }

    @Override
    public List<MessageEntity> findAllByTime(long from, long to) {
        return redisTemplate.opsForZSet()
                .rangeByScore(MESSAGES_KEY, from, to)
                .stream()
                .collect(toList());
    }

    @Override
    public boolean removeAll() {
        return redisTemplate.delete(MESSAGES_KEY);
    }
}
