package com.smit.RealTimeChat.controller;

import com.smit.RealTimeChat.dto.ChatMessage;
import com.smit.RealTimeChat.dto.MessageResponse;
import com.smit.RealTimeChat.dto.SendMessageRequest;
import com.smit.RealTimeChat.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendMessageRequest request,
            Principal principal
    ) {
        String senderEmail = principal.getName();

        MessageResponse saved = messageService.sendMessage(roomId, senderEmail, request);

        ChatMessage chatMessage = new ChatMessage(
                saved.getId(),
                saved.getContent(),
                saved.getSenderEmail(),
                saved.getSenderUsername(),
                saved.getChatRoomId(),
                saved.getCreatedAt(),
                saved.isDelivered(),
                saved.getDeliveredAt(),
                saved.isSeen(),
                saved.getSeenAt(),
                saved.getFileName(),
                saved.getFileUrl(),
                saved.getFileType()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessage);
    }
}