package org.citywatcher.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.citywatcher.dto.IssueNotificationDTO;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@ServerEndpoint("/ws/issues/{username}")
@Component
public class IssueWebSocketServer {
    private static WebSocketManager webSocketManager;

    @Autowired
    public void setWebSocketManager(WebSocketManager manager) {
        IssueWebSocketServer.webSocketManager = manager;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        webSocketManager.addSession(username, session);
        System.out.println("New WebSocket connection: " + username);
    }

    @OnClose
    public void onClose(Session session) {
        webSocketManager.removeSession(session);
        System.out.println("WebSocket connection closed.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = webSocketManager.getUsernameBySession(session);
        System.out.println("WebSocket error for " + username + ": " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String username = webSocketManager.getUsernameBySession(session);
        System.out.println("Received message from " + username + ": " + message);
    }

    public void sendIssueStatusUpdate(Issue updatedIssue) {
        IssueNotificationDTO notificationDTO = new IssueNotificationDTO(updatedIssue, "UPDATE");
        String jsonMessage = webSocketManager.convertToJson(notificationDTO);

        // Send to the reporter
        webSocketManager.sendToUser(updatedIssue.getReporter().getUsername(), jsonMessage);

        // Send to the assigned official
        if (updatedIssue.getAssignedOfficial() != null) {
            webSocketManager.sendToUser(updatedIssue.getAssignedOfficial().getUsername(), jsonMessage);
        }

        List<User> volunteers = updatedIssue.getVolunteers();
        for(User user : volunteers) {
            webSocketManager.sendToUser(user.getUsername(), jsonMessage);
        }

        // Send to subscribed users
        broadcastToSubscribers(updatedIssue, jsonMessage);
    }

    public void sendAssignmentNotification(Issue assignedIssue) {
        if (assignedIssue.getAssignedOfficial() != null) {
            IssueNotificationDTO notificationDTO = new IssueNotificationDTO(assignedIssue, "ASSIGNMENT");
            String jsonMessage = webSocketManager.convertToJson(notificationDTO);
            webSocketManager.sendToUser(assignedIssue.getAssignedOfficial().getUsername(), jsonMessage);
        }
    }

    public void sendCommentNotification(Issue issue, String comment) {
        IssueNotificationDTO notificationDTO = new IssueNotificationDTO(issue, "COMMENT");
        notificationDTO.setComment(comment);
        String jsonMessage = webSocketManager.convertToJson(notificationDTO);

        // Send to the reporter
        webSocketManager.sendToUser(issue.getReporter().getUsername(), jsonMessage);

        // Send to the assigned official
        if (issue.getAssignedOfficial() != null) {
            webSocketManager.sendToUser(issue.getAssignedOfficial().getUsername(), jsonMessage);
        }

        //Send to the volunteers
        List<User> volunteers = issue.getVolunteers();
        for(User user : volunteers) {
            webSocketManager.sendToUser(user.getUsername(), jsonMessage);
        }

        // Send to subscribed users
        List<User> subscribedUsers = getSubscribedUsers(issue);
        for (User user : subscribedUsers) {
            webSocketManager.sendToUser(user.getUsername(), jsonMessage);
        }
    }

    private List<User> getSubscribedUsers(Issue issue) {
        // Implement logic to get users subscribed to this issue
        return new ArrayList<User>();
    }

    public void broadcastToSubscribers(Issue issue, String message) {
        List<User> subscribers = getSubscribedUsers(issue);
        for (User subscriber : subscribers) {
            webSocketManager.sendToUser(subscriber.getUsername(), message);
        }
    }
}