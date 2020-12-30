package com.skf.messages.web;

import com.skf.messages.domain.dto.Message;
import com.skf.messages.domain.dto.TimeInterval;
import com.skf.messages.web.dto.MessageRequest;
import com.skf.messages.service.MessageService;
import com.skf.messages.web.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Validated
    @PostMapping(value = "publish")
    public void publish(@RequestBody MessageRequest message) {
        messageService.addMessage(message.getContent());
    }

    @GetMapping(value = "getLast")
    public MessageResponse getLast() {
        Message message = messageService.getLastMessage();
        return MessageResponse.create(message);
    }

    @GetMapping("getByTime")
    public List<String> getByTime(@RequestParam long start, @RequestParam long end) {
        List<Message> messages = messageService.getMessagesByTimeInterval(new TimeInterval(start, end));
        return messages.stream().map(message -> message.getContent()).collect(Collectors.toList());
    }
}
