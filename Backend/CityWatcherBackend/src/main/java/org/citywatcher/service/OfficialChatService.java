package org.citywatcher.service;

import org.citywatcher.model.Message;
import java.time.LocalDateTime;
import java.util.List;

public interface OfficialChatService {
    Message sendMessage(Long userId, Message message);
    List<Message> getMessages(LocalDateTime since, int limit);
    void deleteMessage(Long userId, Long messageId);
}