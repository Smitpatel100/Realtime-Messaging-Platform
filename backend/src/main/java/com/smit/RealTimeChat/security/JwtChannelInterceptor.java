package com.smit.RealTimeChat.security;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public JwtChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String bearerToken = authHeaders.get(0);

                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);

                    try {
                        String email = jwtService.extractEmail(token);

                        if (email != null) {
                            Principal principal = new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.emptyList()
                            );
                            accessor.setUser(principal);
                        }
                    } catch (Exception e) {
                        // Invalid token — connection proceeds without authenticated principal
                    }
                }
            }
        }

        return message;
    }
}