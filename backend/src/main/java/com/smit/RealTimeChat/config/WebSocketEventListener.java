package com.smit.RealTimeChat.config;
import com.smit.RealTimeChat.service.MessageService;
import com.smit.RealTimeChat.service.PresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.security.Principal;
@Component
public class WebSocketEventListener {
    private final PresenceService presenceService;
    private final MessageService messageService;
    public WebSocketEventListener(PresenceService presenceService, MessageService messageService) {
        this.presenceService = presenceService;
        this.messageService = messageService;
    }
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();
        if (principal != null) {
            presenceService.userConnected(sessionId, principal.getName());
            messageService.markPendingMessagesAsDeliveredOnReconnect(principal.getName());
        }
    }
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        presenceService.userDisconnected(sessionId);
    }
}	