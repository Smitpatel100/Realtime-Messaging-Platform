package com.smit.RealTimeChat.controller;
import com.smit.RealTimeChat.dto.AddMemberRequest;
import com.smit.RealTimeChat.dto.ChatRoomResponse;
import com.smit.RealTimeChat.dto.CreateGroupChatRequest;
import com.smit.RealTimeChat.dto.CreatePrivateChatRequest;
import com.smit.RealTimeChat.dto.UnreadCountResponse;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.exception.UserNotFoundException;
import com.smit.RealTimeChat.repository.ChatRoomRepository;
import com.smit.RealTimeChat.repository.MessageRepository;
import com.smit.RealTimeChat.repository.UserRepository;
import com.smit.RealTimeChat.service.ChatRoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    public ChatRoomController(
            ChatRoomService chatRoomService,
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.chatRoomService = chatRoomService;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }
    @PostMapping("/private")
    public ResponseEntity<ChatRoomResponse> createPrivateChat(
            @Valid @RequestBody CreatePrivateChatRequest request,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        ChatRoomResponse response = chatRoomService.createPrivateChat(currentUserEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/group")
    public ResponseEntity<ChatRoomResponse> createGroupChat(
            @Valid @RequestBody CreateGroupChatRequest request,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        ChatRoomResponse response = chatRoomService.createGroupChat(currentUserEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/{roomId}/members")
    public ResponseEntity<ChatRoomResponse> addMember(
            @PathVariable Long roomId,
            @Valid @RequestBody AddMemberRequest request
    ) {
        ChatRoomResponse response = chatRoomService.addMember(roomId, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        List<ChatRoomResponse> rooms = chatRoomService.getUserChatRooms(currentUserEmail);
        return ResponseEntity.ok(rooms);
    }
    @GetMapping("/unread-counts")
    public ResponseEntity<List<UnreadCountResponse>> getUnreadCounts(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        List<ChatRoom> rooms = chatRoomRepository.findByUsersContaining(user);
        List<UnreadCountResponse> counts = rooms.stream()
                .map(room -> new UnreadCountResponse(
                        room.getId(),
                        messageRepository.countByChatRoomAndSeenFalseAndSenderEmailNot(room, currentUserEmail)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(counts);
    }
}