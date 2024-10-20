package org.citywatcher.controller;

import org.citywatcher.dto.IssueNotificationDTO;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendIssueStatusUpdate(Issue updatedIssue) {
        IssueNotificationDTO notificationDTO = new IssueNotificationDTO(updatedIssue, "UPDATE");

        // Send to the reporter
        sendToUser(updatedIssue.getReporter().getUsername(), "/queue/issue-notifications", notificationDTO);

        // Send to the assigned official
        if (updatedIssue.getAssignedOfficial() != null) {
            sendToUser(updatedIssue.getAssignedOfficial().getUsername(), "/queue/issue-notifications", notificationDTO);
        }

        // Send to subscribed users
        List<User> subscribedUsers = getSubscribedUsers(updatedIssue);
        for (User user : subscribedUsers) {
            sendToUser(user.getUsername(), "/queue/issue-updates", notificationDTO);
        }
    }

    public void sendAssignmentNotification(Issue assignedIssue) {
        if (assignedIssue.getAssignedOfficial() != null) {
            IssueNotificationDTO notificationDTO = new IssueNotificationDTO(assignedIssue, "ASSIGNMENT");
            sendToUser(assignedIssue.getAssignedOfficial().getUsername(), "/queue/issue-notifications", notificationDTO);
        }
    }

    private void sendToUser(String username, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }

    private List<User> getSubscribedUsers(Issue issue) {
        // Implement logic to get users subscribed to this issue
        // This could be based on a many-to-many relationship between User and Issue
        // or a separate Subscription entity
        // For now, we'll return an empty list
        return new ArrayList<>();
    }
}
