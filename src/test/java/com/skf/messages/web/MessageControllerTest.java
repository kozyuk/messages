package com.skf.messages.web;

import com.skf.messages.domain.dto.Message;
import com.skf.messages.domain.dto.TimeInterval;
import com.skf.messages.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.skf.messages.domain.Messages.fromMessageEntities;
import static com.skf.messages.repository.entity.MessageEntity.createEntity;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MessageService messageService;

    @Test
    void publish() throws Exception {
        when(messageService.addMessage("This is content of message"))
                .thenReturn(new Message(createEntity("This is content of message")));

        mockMvc.perform(
                post("/publish")
                        .content("{\"content\" : \"This is content of message\"}")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getLast() throws Exception {
        when(messageService.getLastMessage())
                .thenReturn(new Message(createEntity("This is the last content of message")));

        mockMvc.perform(
                get("/getLast")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":\"This is the last content of message\"}"));

    }

    @Test
    void getByTime() throws Exception {
        long start = System.currentTimeMillis();
        long end = start + 24 * 60 * 60 * 1000;
        when(messageService.getMessagesByTimeInterval(new TimeInterval(start, end)))
                .thenReturn(fromMessageEntities(createEntity("content one"),
                        createEntity("content two"),
                        createEntity("content three")).toList());

        mockMvc.perform(
                get("/getByTime")
                        .param("start", Long.toString(start))
                        .param("end", Long.toString(end))
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"content one\", \"content two\", \"content three\"]"));
    }
}