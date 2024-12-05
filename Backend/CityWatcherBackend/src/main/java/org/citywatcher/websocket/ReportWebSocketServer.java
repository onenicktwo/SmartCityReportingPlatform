package org.citywatcher.websocket;

import org.citywatcher.dto.ReportNotificationDTO;
import org.citywatcher.model.Report;
import org.citywatcher.websocket.WebSocketManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

@ServerEndpoint("/ws/reports/{username}")
@Component
public class ReportWebSocketServer {
    private static WebSocketManager webSocketManager;

    @Autowired
    public void setWebSocketManager(WebSocketManager manager) {
        ReportWebSocketServer.webSocketManager = manager;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        webSocketManager.addSession(username, session);
        System.out.println("New WebSocket connection for reports: " + username);
    }

    @OnClose
    public void onClose(Session session) {
        webSocketManager.removeSession(session);
        System.out.println("WebSocket connection for reports closed.");
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

    public void sendReportNotification(Report report) {
        ReportNotificationDTO notificationDTO = new ReportNotificationDTO(report, "NEW_REPORT");
        String jsonMessage = webSocketManager.convertToJson(notificationDTO);

        List<String> adminUsernames = webSocketManager.getAdminUsernames();
        for (String admin : adminUsernames) {
            Session adminSession = webSocketManager.getSessionByUsername(admin);
            if (adminSession != null && adminSession.isOpen()) {
                try {
                    adminSession.getBasicRemote().sendText(jsonMessage);
                } catch (Exception e) {
                    System.err.println("Error sending notification to admin " + admin + ": " + e.getMessage());
                }
            }
        }
    }
}
