package com.smit.RealTimeChat.controller;

import com.smit.RealTimeChat.dto.TypingMessage;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.repository.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TypingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public TypingController(SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.typing/{roomId}")
    public void handleTyping(
            @DestinationVariable Long roomId,
            @Payload TypingMessage request,
            Principal principal
    ) {
        String senderEmail = principal.getName();

        String senderUsername = userRepository.findByEmail(senderEmail)
                .map(User::getUsername)
                .orElse(senderEmail);

        TypingMessage message = new TypingMessage(
                roomId,
                senderEmail,
                senderUsername,
                request.isTyping()
        );

        messagingTemplate.convertAndSend("/topic/typing/" + roomId, message);
    }
}