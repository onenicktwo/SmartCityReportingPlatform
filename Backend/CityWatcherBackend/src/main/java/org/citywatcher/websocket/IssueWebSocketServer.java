package org.citywatcher.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.citywatcher.dto.IssueNotificationDTO;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@ServerEndpoint("/ws/issues/{username}")
@Component
public class IssueWebSocketServer {

    private static Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        System.out.println("New WebSocket connection: " + username);
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        System.out.println("WebSocket connection closed: " + username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUsernameMap.get(session);
        System.out.println("WebSocket error for " + username + ": " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String username = sessionUsernameMap.get(session);
        System.out.println("Received message from " + username + ": " + message);
    }

    public void sendIssueStatusUpdate(Issue updatedIssue) {
        IssueNotificationDTO notificationDTO = new IssueNotificationDTO(updatedIssue, "UPDATE");
        // Send to the reporter
        sendToUser(updatedIssue.getReporter().getUsername(), notificationDTO);

        // Send to the assigned official
        if (updatedIssue.getAssignedOfficial() != null) {
            sendToUser(updatedIssue.getAssignedOfficial().getUsername(), notificationDTO);
        }

        // Send to subscribed users
        List<User> subscribedUsers = getSubscribedUsers(updatedIssue);
        for (User user : subscribedUsers) {
            sendToUser(user.getUsername(), notificationDTO);
        }
    }

    public void sendAssignmentNotification(Issue assignedIssue) {
        if (assignedIssue.getAssignedOfficial() != null) {
            IssueNotificationDTO notificationDTO = new IssueNotificationDTO(assignedIssue, "ASSIGNMENT");
            sendToUser(assignedIssue.getAssignedOfficial().getUsername(), notificationDTO);
        }
    }

    private void sendToUser(String username, Object payload) {
        Session session = usernameSessionMap.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(convertToJson(payload));
            } catch (IOException e) {
                System.out.println("Error sending message to " + username + ": " + e.getMessage());
            }
        }
    }

    private String convertToJson(Object object) {
        // Implement JSON conversion here. Use libraries like Jackson or Gson.
        return object.toString();
    }

    private List<User> getSubscribedUsers(Issue issue) {
        // Implement logic to get users subscribed to this issue
        return new ArrayList<User>();
    }

    public void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.out.println("Error broadcasting message to " + username + ": " + e.getMessage());
            }
        });
    }

    public void broadcastToSubscribers(Issue issue, String message) {
        List<User> subscribers = getSubscribedUsers(issue);
        for (User subscriber : subscribers) {
            sendToUser(subscriber.getUsername(), message);
        }
    }

    public boolean isUserConnected(String username) {
        return usernameSessionMap.containsKey(username);
    }

    public int getConnectedUsersCount() {
        return usernameSessionMap.size();
    }

    public List<String> getConnectedUsernames() {
        return new ArrayList<>(usernameSessionMap.keySet());
    }

    public void disconnectUser(String username) {
        Session session = usernameSessionMap.get(username);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                System.out.println("Error disconnecting user " + username + ": " + e.getMessage());
            }
        }
    }
}