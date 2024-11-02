package org.citywatcher.service;

import org.citywatcher.model.Comment;

import java.util.List;

public interface OfficialChatService {
    Comment sendMessage(Long userId, Long issueId, Comment message);
    List<Comment> getOfficialMessages(Long userId, Long issueId);
    void deleteMessage(Long userId, Long messageId);
}
