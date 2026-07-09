package com.smit.RealTimeChat.service;

import com.smit.RealTimeChat.dto.PresenceMessage;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // sessionId -> email   (tracks which session belongs to which user)
    private final Map<String, String> sessionToEmail = new ConcurrentHashMap<>();

    // email -> active session count   (handles multiple tabs/devices per user)
    private final Map<String, Integer> emailSessionCount = new ConcurrentHashMap<>();

    // email -> username   (cache to avoid DB lookup on every disconnect)
    private final Map<String, String> emailToUsername = new ConcurrentHashMap<>();

    public PresenceService(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void userConnected(String sessionId, String email) {
        if (email == null) return;

        sessionToEmail.put(sessionId, email);

        int currentCount = emailSessionCount.getOrDefault(email, 0);
        emailSessionCount.put(email, currentCount + 1);

        // Only broadcast ONLINE on the first session for this user
        if (currentCount == 0) {
            String username = resolveUsername(email);
            broadcastPresence(email, username, true);
        }
    }

    public void userDisconnected(String sessionId) {
        String email = sessionToEmail.remove(sessionId);
        if (email == null) return;

        int currentCount = emailSessionCount.getOrDefault(email, 1);
        int newCount = currentCount - 1;

        if (newCount <= 0) {
            emailSessionCount.remove(email);
            String username = emailToUsername.getOrDefault(email, email);
            broadcastPresence(email, username, false);
        } else {
            emailSessionCount.put(email, newCount);
        }
    }

    public Collection<String> getOnlineUsers() {
        return emailSessionCount.keySet();
    }

    public boolean isOnline(String email) {
        return emailSessionCount.containsKey(email);
    }

    private String resolveUsername(String email) {
        String cached = emailToUsername.get(email);
        if (cached != null) return cached;

        String username = userRepository.findByEmail(email)
                .map(User::getUsername)
                .orElse(email);

        emailToUsername.put(email, username);
        return username;
    }

    private void broadcastPresence(String email, String username, boolean online) {
        PresenceMessage message = new PresenceMessage(email, username, online);
        messagingTemplate.convertAndSend("/topic/presence", message);
    }
}