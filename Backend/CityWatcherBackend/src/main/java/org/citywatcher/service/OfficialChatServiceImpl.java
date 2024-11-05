package org.citywatcher.service;

import org.citywatcher.model.*;
import org.citywatcher.repository.CommentsRepository;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.MessageRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfficialChatServiceImpl implements OfficialChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public OfficialChatServiceImpl(
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message sendMessage(Long userId, Message message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateOfficialAccess(user);

        message.setSenderId(userId);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(LocalDateTime since, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if (since != null) {
            return messageRepository.findByTimestampGreaterThanOrderByTimestampDesc(since, pageable);
        }
        return messageRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    public void deleteMessage(Long userId, Long messageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateOfficialAccess(user);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (user.getRole() != UserRole.ADMIN &&
                !message.getSenderId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own messages");
        }

        messageRepository.delete(message);
    }

    private void validateOfficialAccess(User user) {
        if (user.getRole() != UserRole.CITY_OFFICIAL &&
                user.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only city officials and administrators can access official chat");
        }
    }
}
