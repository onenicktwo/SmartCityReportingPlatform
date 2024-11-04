package org.citywatcher.controller;

import org.citywatcher.model.Comment;
import org.citywatcher.service.OfficialChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/citywatcher/official-chat")
public class OfficialChatController {

    private final OfficialChatService officialChatService;

    @Autowired
    public OfficialChatController(OfficialChatService officialChatService) {
        this.officialChatService = officialChatService;
    }

    @PostMapping("/users/{userId}/issues/{issueId}/messages")
    public ResponseEntity<Comment> sendMessage(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @RequestBody Comment message) {
        message.setInternalNote(true);
        Comment sentMessage = officialChatService.sendMessage(userId, issueId, message);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/issues/{issueId}/messages")
    public ResponseEntity<List<Comment>> getOfficialMessages(
            @PathVariable Long userId,
            @PathVariable Long issueId) {
        List<Comment> messages = officialChatService.getOfficialMessages(userId, issueId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long userId,
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