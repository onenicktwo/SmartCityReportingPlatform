package org.citywatcher.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketManager {

    public static ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private final Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private UserRepository userRepository;

    public void addSession(String username, Session session) {
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
    }

    public void removeSession(Session session) {
        String username = sessionUsernameMap.get(session);
        if (username != null) {
            sessionUsernameMap.remove(session);
            usernameSessionMap.remove(username);
        }
    }

    public Session getSessionByUsername(String username) {
        return usernameSessionMap.get(username);
    }

    public String getUsernameBySession(Session session) {
        return sessionUsernameMap.get(session);
    }

    public boolean isUserConnected(String username) {
        return usernameSessionMap.containsKey(username);
    }

    public void sendToUser(String username, String message) {
        Session session = usernameSessionMap.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            System.out.println("Error converting object to JSON: " + e.getMessage());
            return "{}";  // Return an empty JSON object in case of error
        }
    }

    public void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void broadcastToOfficials(String message) {
        usernameSessionMap.forEach((username, session) -> {
            try {
                userRepository.findByUsername(username).ifPresent(user -> {
                    if (user.getRole() == UserRole.CITY_OFFICIAL || user.getRole() == UserRole.ADMIN) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getAdminUsernames() {
        List<String> adminUsernames = new ArrayList<>();

        usernameSessionMap.keySet().forEach(username -> {
            try {
                userRepository.findByUsername(username).ifPresent(user -> {
                    if (user.getRole() == UserRole.ADMIN) {
                        adminUsernames.add(username);
                    }
                });
            } catch (Exception e) {
                System.err.println("Error checking role for user " + username + ": " + e.getMessage());
            }
        });

        return adminUsernames;
    }
}
