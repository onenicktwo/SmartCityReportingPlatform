package org.citywatcher.controller;

import org.citywatcher.model.Message;
import org.citywatcher.service.OfficialChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/citywatcher/official-chat")
public class OfficialChatController {

    private final OfficialChatService officialChatService;

    @Autowired
    public OfficialChatController(OfficialChatService officialChatService) {
        this.officialChatService = officialChatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long userId,
            @RequestBody Message message) {
        Message sentMessage = officialChatService.sendMessage(userId, message);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam(required = false) LocalDateTime since,
            @RequestParam(defaultValue = "50") int limit) {
        List<Message> messages = officialChatService.getMessages(since, limit);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @RequestParam Long userId,
            @PathVariable Long messageId) {
        try {
            officialChatService.deleteMessage(userId, messageId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}