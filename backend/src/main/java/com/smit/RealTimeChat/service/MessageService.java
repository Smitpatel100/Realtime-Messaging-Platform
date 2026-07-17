package com.smit.RealTimeChat.service;
import com.smit.RealTimeChat.dto.ChatMessage;
import com.smit.RealTimeChat.dto.MessageResponse;
import com.smit.RealTimeChat.dto.SendMessageRequest;
import com.smit.RealTimeChat.dto.UnreadCountUpdate;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.DeletedMessage;
import com.smit.RealTimeChat.entity.Message;
import com.smit.RealTimeChat.entity.RoomClearState;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.exception.UserNotFoundException;
import com.smit.RealTimeChat.repository.ChatRoomRepository;
import com.smit.RealTimeChat.repository.DeletedMessageRepository;
import com.smit.RealTimeChat.repository.MessageRepository;
import com.smit.RealTimeChat.repository.RoomClearStateRepository;
import com.smit.RealTimeChat.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final DeletedMessageRepository deletedMessageRepository;
    private final RoomClearStateRepository roomClearStateRepository;
    public MessageService(
            MessageRepository messageRepository,
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            PresenceService presenceService,
            SimpMessagingTemplate messagingTemplate,
            DeletedMessageRepository deletedMessageRepository,
            RoomClearStateRepository roomClearStateRepository
    ) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
        this.deletedMessageRepository = deletedMessageRepository;
        this.roomClearStateRepository = roomClearStateRepository;
    }
    @Transactional
    public MessageResponse sendMessage(Long roomId, String senderEmail, SendMessageRequest request) {
        boolean hasText = request.getContent() != null && !request.getContent().isBlank();
        boolean hasFile = request.getFileUrl() != null && !request.getFileUrl().isBlank();
        if (!hasText && !hasFile) {
            throw new IllegalArgumentException("Message must contain text or a file attachment");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + senderEmail));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(senderEmail));
        if (!isMember) {
            throw new RuntimeException("User is not a member of this chat room");
        }
        Message message = new Message();
        message.setContent(hasText ? request.getContent() : null);
        message.setSender(sender);
        message.setChatRoom(chatRoom);
        if (hasFile) {
            message.setFileName(request.getFileName());
            message.setFileUrl(request.getFileUrl());
            message.setFileType(request.getFileType());
        }
        Message saved = messageRepository.save(message);
        // Delivered: true if at least one other room member is currently online
        boolean anyoneElseOnline = chatRoom.getUsers().stream()
                .filter(user -> !user.getEmail().equals(senderEmail))
                .anyMatch(user -> presenceService.isOnline(user.getEmail()));
        if (anyoneElseOnline) {
            saved.setDelivered(true);
            saved.setDeliveredAt(LocalDateTime.now());
            saved = messageRepository.save(saved);
        }

        String preview;
        if (hasText) {
            String trimmedContent = request.getContent().trim();
            preview = trimmedContent.length() > 60 ? trimmedContent.substring(0, 60) + "..." : trimmedContent;
        } else {
            preview = "📎 Sent an attachment";
        }

        UnreadCountUpdate update = new UnreadCountUpdate(
                chatRoom.getId(),
                chatRoom.getName(),
                sender.getEmail(),
                sender.getUsername(),
                preview
        );
        chatRoom.getUsers().stream()
                .filter(user -> !user.getEmail().equals(senderEmail))
                .forEach(user -> messagingTemplate.convertAndSendToUser(
                        user.getEmail(), "/queue/unread-count", update));

        return mapToResponse(saved);
    }
    public List<MessageResponse> getChatHistory(Long roomId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("User is not a member of this chat room");
        }
        List<Message> messages = messageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        Set<Long> hiddenIds = new HashSet<>(
                deletedMessageRepository.findMessageIdsByUserAndChatRoom(currentUser, chatRoom));
        LocalDateTime clearCutoff = roomClearStateRepository.findByUserAndRoom(currentUser, chatRoom)
                .map(RoomClearState::getClearedAt)
                .orElse(null);
        return messages.stream()
                .filter(m -> !hiddenIds.contains(m.getId()))
                .filter(m -> clearCutoff == null || m.getCreatedAt().isAfter(clearCutoff))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public MessageResponse markMessageAsSeen(Long messageId, String currentUserEmail) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));
        if (message.getSender().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Sender cannot mark their own message as seen");
        }
        boolean changed = false;
        if (!message.isDelivered()) {
            message.setDelivered(true);
            message.setDeliveredAt(LocalDateTime.now());
            changed = true;
        }
        if (!message.isSeen()) {
            message.setSeen(true);
            message.setSeenAt(LocalDateTime.now());
            changed = true;
        }
        if (changed) {
            message = messageRepository.save(message);
            ChatMessage broadcast = mapToChatMessage(message);
            messagingTemplate.convertAndSend("/topic/chat/" + message.getChatRoom().getId(), broadcast);
        }
        return mapToResponse(message);
    }
    @Transactional
    public List<MessageResponse> markMessagesAsSeen(Long roomId, String currentUserEmail) {
        userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("User is not a member of this chat room");
        }
        List<Message> unseenMessages = messageRepository
                .findByChatRoomAndSeenFalseAndSenderEmailNot(chatRoom, currentUserEmail);
        if (unseenMessages.isEmpty()) {
            return List.of();
        }
        LocalDateTime now = LocalDateTime.now();
        for (Message message : unseenMessages) {
            if (!message.isDelivered()) {
                message.setDelivered(true);
                message.setDeliveredAt(now);
            }
            message.setSeen(true);
            message.setSeenAt(now);
        }
        List<Message> saved = messageRepository.saveAll(unseenMessages);
        List<MessageResponse> responses = saved.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        for (Message message : saved) {
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, mapToChatMessage(message));
        }
        return responses;
    }
    public List<MessageResponse> searchMessages(Long roomId, String currentUserEmail, String keyword) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));
        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("User is not a member of this chat room");
        }
        List<Message> matches = messageRepository
                .findByChatRoomAndContentContainingIgnoreCaseOrderByCreatedAtAsc(chatRoom, keyword);
        Set<Long> hiddenIds = new HashSet<>(
                deletedMessageRepository.findMessageIdsByUserAndChatRoom(currentUser, chatRoom));
        LocalDateTime clearCutoff = roomClearStateRepository.findByUserAndRoom(currentUser, chatRoom)
                .map(RoomClearState::getClearedAt)
                .orElse(null);
        return matches.stream()
                .filter(m -> !hiddenIds.contains(m.getId()))
                .filter(m -> clearCutoff == null || m.getCreatedAt().isAfter(clearCutoff))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteMessageForMe(Long messageId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        boolean isMember = message.getChatRoom().getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("You are not a member of this chat room");
        }

        boolean alreadyDeleted = deletedMessageRepository.findByUserAndMessage(currentUser, message).isPresent();
        if (!alreadyDeleted) {
            deletedMessageRepository.save(new DeletedMessage(currentUser, message));
        }
    }
    @Transactional
    public void clearChat(Long roomId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));

        boolean isMember = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("You are not a member of this chat room");
        }

        LocalDateTime now = LocalDateTime.now();
        RoomClearState state = roomClearStateRepository.findByUserAndRoom(currentUser, chatRoom)
                .orElse(new RoomClearState(currentUser, chatRoom, now));
        state.setClearedAt(now);
        roomClearStateRepository.save(state);
    }
    @Transactional
    public void markPendingMessagesAsDeliveredOnReconnect(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return;

        List<ChatRoom> rooms = chatRoomRepository.findByUsersContaining(user);
        LocalDateTime now = LocalDateTime.now();

        for (ChatRoom room : rooms) {
            List<Message> pending = messageRepository
                    .findByChatRoomAndDeliveredFalseAndSenderEmailNot(room, userEmail);
            if (pending.isEmpty()) continue;

            for (Message message : pending) {
                message.setDelivered(true);
                message.setDeliveredAt(now);
            }
            List<Message> saved = messageRepository.saveAll(pending);
            for (Message message : saved) {
                messagingTemplate.convertAndSend("/topic/chat/" + room.getId(), mapToChatMessage(message));
            }
        }
    }
    @Transactional
    public void deleteMessage(Long messageId, String currentUserEmail) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        if (!message.getSender().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Only the sender can delete this message");
        }

        Long roomId = message.getChatRoom().getId();
        messageRepository.delete(message);

        ChatMessage deletedEvent = ChatMessage.deletedEvent(messageId, roomId, currentUserEmail);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, deletedEvent);
    }
    private MessageResponse mapToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSender().getEmail(),
                message.getSender().getUsername(),
                message.getChatRoom().getId(),
                message.getCreatedAt(),
                message.isDelivered(),
                message.getDeliveredAt(),
                message.isSeen(),
                message.getSeenAt(),
                message.getFileName(),
                message.getFileUrl(),
                message.getFileType()
        );
    }
    public ChatMessage mapToChatMessage(Message message) {
        return new ChatMessage(
                message.getId(),
                message.getContent(),
                message.getSender().getEmail(),
                message.getSender().getUsername(),
                message.getChatRoom().getId(),
                message.getCreatedAt(),
                message.isDelivered(),
                message.getDeliveredAt(),
                message.isSeen(),
                message.getSeenAt(),
                message.getFileName(),
                message.getFileUrl(),
                message.getFileType()
        );
    }
}