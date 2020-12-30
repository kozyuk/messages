package com.skf.messages.web;


import com.skf.messages.repository.MessageRepository;
import com.skf.messages.repository.entity.MessageEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
class MessageControllerIntegrationTest {

    @Container
    static GenericContainer redis = new GenericContainer<>("redis:6.0.9-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.redis.host", redis::getHost);
    }

    @Autowired
    WebApplicationContext context;

    @Autowired
    MessageRepository messageRepository;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        messageRepository.removeAll();
    }

    @Test
    void messageShouldBePublished() throws Exception {
        mockMvc.perform(
                post("/publish")
                        .content("{\"content\" : \"This is content of message\"}")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void lastMessageMayBeRetrieved() throws Exception {
        messageRepository.save(MessageEntity.createEntity("This is the last content of message"));
        mockMvc.perform(
                get("/getLast")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\" : \"This is the last content of message\"}"));
    }

    @Test
    void allMessagesByTimeIntervalMayBeRetrieved() throws Exception {
        long start = System.currentTimeMillis();
        messageRepository.save(MessageEntity.createEntity("content"));
        messageRepository.save(MessageEntity.createEntity("content 2"));
        messageRepository.save(MessageEntity.createEntity("content 3"));
        messageRepository.save(MessageEntity.createEntity("content 4"));
        messageRepository.save(MessageEntity.createEntity("content"));
        long end = System.currentTimeMillis();

        mockMvc.perform(
                get("/getByTime")
                        .param("start", Long.toString(start))
                        .param("end", Long.toString(end))
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"content\", \"content 2\", \"content 3\", \"content 4\", \"content\"]"));
    }
}