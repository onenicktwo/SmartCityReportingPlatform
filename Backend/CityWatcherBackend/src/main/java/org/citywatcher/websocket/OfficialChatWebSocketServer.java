package org.citywatcher.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.citywatcher.model.Message;
import org.citywatcher.service.OfficialChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ServerEndpoint("/ws/official-chat/{username}")
@Component
public class OfficialChatWebSocketServer {

    private static WebSocketManager webSocketManager;
    private static OfficialChatService officialChatService;

    @Autowired
    public void setWebSocketManager(WebSocketManager manager) {
        OfficialChatWebSocketServer.webSocketManager = manager;
    }

    @Autowired
    public void setOfficialChatService(OfficialChatService chatService) {
        OfficialChatWebSocketServer.officialChatService = chatService;
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
    public void onMessage(String message, Session session) throws IOException {
        String username = webSocketManager.getUsernameBySession(session);
        System.out.println("Received message from " + username + ": " + message);

        Message chatMessage = webSocketManager.objectMapper.readValue(message, Message.class);

        Message savedMessage = officialChatService.sendMessage(chatMessage.getSenderId(), chatMessage);

        // Broadcast the saved message to all connected officials
        broadcastToOfficials(savedMessage);
    }

    private void broadcastToOfficials(Message message) {
        String jsonMessage = webSocketManager.convertToJson(message);
        webSocketManager.broadcastToOfficials(jsonMessage);
    }
}