package org.citywatcher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Official Chat", description = "APIs for official chat management")
public class OfficialChatController {

    private final OfficialChatService officialChatService;

    @Autowired
    public OfficialChatController(OfficialChatService officialChatService) {
        this.officialChatService = officialChatService;
    }

    @Operation(summary = "Send a message", description = "Official/admin user can post a message to the chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid message data", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized", content = @Content)
    })
    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long userId,
            @RequestBody Message message) {
        Message sentMessage = officialChatService.sendMessage(userId, message);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    @Operation(summary = "Get official/admin chat messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content)
    })
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam(required = false) LocalDateTime since,
            @RequestParam(defaultValue = "50") int limit) {
        List<Message> messages = officialChatService.getMessages(since, limit);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Operation(summary = "Delete a chat message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or message not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete the message", content = @Content)
    })
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