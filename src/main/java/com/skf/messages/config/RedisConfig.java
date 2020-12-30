package com.skf.messages.config;

import com.skf.messages.repository.entity.MessageEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, MessageEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MessageEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer(MessageEntity.class));
        return template;
    }
}
