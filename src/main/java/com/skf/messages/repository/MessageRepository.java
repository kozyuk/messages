package com.skf.messages.repository;


import com.skf.messages.repository.entity.MessageEntity;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {

    void save(MessageEntity entity);

    Optional<MessageEntity> findLast();

    List<MessageEntity> findAllByTime(long from, long to);

    boolean removeAll();
}
